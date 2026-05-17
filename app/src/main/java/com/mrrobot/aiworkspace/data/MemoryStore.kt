package com.mrrobot.aiworkspace.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject

private val Context.memoryDataStore by preferencesDataStore(name = "mr_robot_memory")

enum class MemoryCategory {
    GENERAL,
    LEARNING,
    ERROR,
    PREFERENCE;

    companion object {
        fun fromName(value: String?): MemoryCategory {
            if (value.isNullOrBlank()) return GENERAL
            return runCatching { valueOf(value.uppercase()) }.getOrDefault(GENERAL)
        }
    }
}

data class MemoryEntry(
    val key: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val category: MemoryCategory = MemoryCategory.GENERAL,
    val hitCount: Int = 1,
    val source: String? = null
)

/**
 * Persistent memory store backed by DataStore. Stores facts, preferences, learnings,
 * and errors. Memories that prove useful (hitCount >= 5) become promotion candidates
 * — they can be merged into the user's "soul" (system prompt).
 */
class MemoryStore(private val context: Context) {

    private val mutex = Mutex()

    private object Keys {
        val MEMORIES_JSON = stringPreferencesKey("memories_json")
    }

    /** Live flow of all memory entries. */
    val memoriesFlow: Flow<List<MemoryEntry>> =
        context.memoryDataStore.data.map { prefs ->
            decode(prefs[Keys.MEMORIES_JSON] ?: "")
        }

    suspend fun getAll(): List<MemoryEntry> = memoriesFlow.first()

    /** Memories that have been reinforced enough to suggest promoting into the system prompt. */
    suspend fun getPromotionCandidates(minHits: Int = 5): List<MemoryEntry> =
        getAll().filter { it.hitCount >= minHits }

    suspend fun store(
        key: String,
        content: String,
        category: MemoryCategory = MemoryCategory.GENERAL,
        source: String? = null
    ): MemoryEntry = mutex.withLock {
        val current = getAll().toMutableList()
        val now = System.currentTimeMillis()
        val existingIdx = current.indexOfFirst { it.key.equals(key, ignoreCase = true) }

        val entry = if (existingIdx >= 0) {
            val existing = current[existingIdx]
            val updated = existing.copy(
                content = content,
                updatedAt = now,
                category = category,
                source = source ?: existing.source
            )
            current[existingIdx] = updated
            updated
        } else {
            val newEntry = MemoryEntry(
                key = key.trim(),
                content = content.trim(),
                createdAt = now,
                updatedAt = now,
                category = category,
                source = source
            )
            current.add(newEntry)
            newEntry
        }

        save(current)
        entry
    }

    suspend fun reinforce(key: String): MemoryEntry? = mutex.withLock {
        val current = getAll().toMutableList()
        val idx = current.indexOfFirst { it.key.equals(key, ignoreCase = true) }
        if (idx < 0) return@withLock null
        val updated = current[idx].copy(
            hitCount = current[idx].hitCount + 1,
            updatedAt = System.currentTimeMillis()
        )
        current[idx] = updated
        save(current)
        updated
    }

    suspend fun forget(key: String): Boolean = mutex.withLock {
        val current = getAll().toMutableList()
        val removed = current.removeAll { it.key.equals(key, ignoreCase = true) }
        if (removed) save(current)
        removed
    }

    suspend fun forgetAll() {
        mutex.withLock { save(emptyList()) }
    }

    private suspend fun save(memories: List<MemoryEntry>) {
        context.memoryDataStore.edit { prefs ->
            prefs[Keys.MEMORIES_JSON] = encode(memories)
        }
    }

    private fun encode(memories: List<MemoryEntry>): String {
        val arr = JSONArray()
        memories.forEach { m ->
            arr.put(
                JSONObject()
                    .put("key", m.key)
                    .put("content", m.content)
                    .put("createdAt", m.createdAt)
                    .put("updatedAt", m.updatedAt)
                    .put("category", m.category.name)
                    .put("hitCount", m.hitCount)
                    .put("source", m.source ?: JSONObject.NULL)
            )
        }
        return arr.toString()
    }

    private fun decode(raw: String): List<MemoryEntry> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                MemoryEntry(
                    key = obj.getString("key"),
                    content = obj.getString("content"),
                    createdAt = obj.optLong("createdAt", 0L),
                    updatedAt = obj.optLong("updatedAt", 0L),
                    category = MemoryCategory.fromName(obj.optString("category")),
                    hitCount = obj.optInt("hitCount", 1),
                    source = obj.optString("source").takeIf { it.isNotBlank() && it != "null" }
                )
            }
        }.getOrDefault(emptyList())
    }
}
