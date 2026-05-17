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
import java.util.UUID

private val Context.chatHistoryDataStore by preferencesDataStore(name = "mr_robot_chat_history")

/**
 * A single message inside a stored chat session. We intentionally only persist
 * lightweight role/content/timestamp tuples — attachments are not serialized
 * because they may reference content URIs that no longer resolve later.
 */
data class StoredChatMessage(
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * A chat session — a single conversation thread the user can resume from
 * the chat history drawer.
 */
data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messages: List<StoredChatMessage> = emptyList(),
    val provider: String = "",
    val model: String = ""
) {
    val preview: String
        get() = messages
            .firstOrNull { it.role == "user" }
            ?.content
            ?.lineSequence()
            ?.firstOrNull { it.isNotBlank() }
            ?.trim()
            ?.take(120)
            ?: messages.firstOrNull()?.content?.take(120).orEmpty()

    val messageCount: Int
        get() = messages.size
}

/**
 * DataStore-backed persistence for chat sessions. Sessions are kept newest first
 * (by updatedAt) and are capped at [MAX_SESSIONS] to keep storage bounded.
 */
class ChatHistoryStore(private val context: Context) {

    private val mutex = Mutex()

    companion object {
        const val MAX_SESSIONS = 100
        private const val MAX_TITLE_LENGTH = 60
    }

    private object Keys {
        val SESSIONS_JSON = stringPreferencesKey("chat_sessions_json")
    }

    val sessionsFlow: Flow<List<ChatSession>> =
        context.chatHistoryDataStore.data.map { prefs ->
            decode(prefs[Keys.SESSIONS_JSON] ?: "")
        }

    suspend fun getAll(): List<ChatSession> = sessionsFlow.first()

    suspend fun getSession(id: String): ChatSession? =
        getAll().firstOrNull { it.id == id }

    /**
     * Save (insert or update) a session. If [session.id] already exists, replace
     * it; otherwise insert. The result is ordered newest-first by updatedAt and
     * truncated to [MAX_SESSIONS].
     */
    suspend fun saveSession(session: ChatSession): ChatSession = mutex.withLock {
        val current = getAll().toMutableList()
        val idx = current.indexOfFirst { it.id == session.id }
        val finalSession = session.copy(updatedAt = System.currentTimeMillis())
        if (idx >= 0) {
            current[idx] = finalSession
        } else {
            current.add(0, finalSession)
        }

        val sorted = current
            .sortedByDescending { it.updatedAt }
            .take(MAX_SESSIONS)

        save(sorted)
        finalSession
    }

    suspend fun deleteSession(id: String): Boolean = mutex.withLock {
        val current = getAll().toMutableList()
        val removed = current.removeAll { it.id == id }
        if (removed) save(current)
        removed
    }

    suspend fun deleteAll() {
        mutex.withLock { save(emptyList()) }
    }

    suspend fun renameSession(id: String, newTitle: String): Boolean = mutex.withLock {
        val current = getAll().toMutableList()
        val idx = current.indexOfFirst { it.id == id }
        if (idx < 0) return@withLock false
        current[idx] = current[idx].copy(
            title = newTitle.take(MAX_TITLE_LENGTH).ifBlank { current[idx].title },
            updatedAt = System.currentTimeMillis()
        )
        save(current.sortedByDescending { it.updatedAt })
        true
    }

    private suspend fun save(sessions: List<ChatSession>) {
        context.chatHistoryDataStore.edit { prefs ->
            prefs[Keys.SESSIONS_JSON] = encode(sessions)
        }
    }

    private fun encode(sessions: List<ChatSession>): String {
        val arr = JSONArray()
        sessions.forEach { s ->
            val msgs = JSONArray()
            s.messages.forEach { m ->
                msgs.put(
                    JSONObject()
                        .put("role", m.role)
                        .put("content", m.content)
                        .put("timestamp", m.timestamp)
                )
            }
            arr.put(
                JSONObject()
                    .put("id", s.id)
                    .put("title", s.title)
                    .put("createdAt", s.createdAt)
                    .put("updatedAt", s.updatedAt)
                    .put("messages", msgs)
                    .put("provider", s.provider)
                    .put("model", s.model)
            )
        }
        return arr.toString()
    }

    private fun decode(raw: String): List<ChatSession> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val msgsArr = obj.optJSONArray("messages") ?: JSONArray()
                val msgs = (0 until msgsArr.length()).map { j ->
                    val mo = msgsArr.getJSONObject(j)
                    StoredChatMessage(
                        role = mo.optString("role", "assistant"),
                        content = mo.optString("content", ""),
                        timestamp = mo.optLong("timestamp", 0L)
                    )
                }
                ChatSession(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    title = obj.optString("title", "Untitled chat"),
                    createdAt = obj.optLong("createdAt", 0L),
                    updatedAt = obj.optLong("updatedAt", 0L),
                    messages = msgs,
                    provider = obj.optString("provider", ""),
                    model = obj.optString("model", "")
                )
            }
        }.getOrDefault(emptyList())
    }
}

/**
 * Build a clean session title from the first user prompt. Falls back to a
 * timestamped placeholder if no user prompt exists yet.
 */
fun deriveSessionTitle(messages: List<StoredChatMessage>): String {
    val firstUser = messages.firstOrNull { it.role == "user" }?.content?.trim().orEmpty()
    if (firstUser.isBlank()) return "New chat"
    val firstLine = firstUser.lineSequence().firstOrNull { it.isNotBlank() }?.trim().orEmpty()
    val cleaned = firstLine
        .removePrefix("/")
        .replace(Regex("\\s+"), " ")
        .trim()
    return cleaned.take(60).ifBlank { "New chat" }
}
