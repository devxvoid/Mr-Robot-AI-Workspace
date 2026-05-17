package com.mrrobot.aiworkspace.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.agentStoreDataStore by preferencesDataStore(name = "mr_robot_agents")

/**
 * Persists custom (user-created) agents and the currently active agent ID across
 * app restarts. Built-in agents from [AgentCatalog] are merged in at read time.
 */
class AgentStore(private val context: Context) {

    private object Keys {
        val CUSTOM_AGENTS_JSON = stringPreferencesKey("custom_agents_json")
        val ACTIVE_AGENT_ID = stringPreferencesKey("active_agent_id")
    }

    val customAgentsFlow: Flow<List<Agent>> =
        context.agentStoreDataStore.data.map { prefs ->
            decode(prefs[Keys.CUSTOM_AGENTS_JSON] ?: "")
        }

    val activeAgentIdFlow: Flow<String?> =
        context.agentStoreDataStore.data.map { prefs ->
            prefs[Keys.ACTIVE_AGENT_ID]?.takeIf { it.isNotBlank() }
        }

    suspend fun getCustomAgents(): List<Agent> = customAgentsFlow.first()

    suspend fun getActiveAgentId(): String? = activeAgentIdFlow.first()

    /**
     * Resolve the active agent: search built-in catalog first, then custom agents.
     * Returns null if no agent is active or the active id no longer exists.
     */
    suspend fun getActiveAgent(): Agent? {
        val activeId = getActiveAgentId() ?: return null
        AgentCatalog.builtInAgents.firstOrNull { it.id == activeId }?.let { return it }
        return getCustomAgents().firstOrNull { it.id == activeId }
    }

    suspend fun saveCustomAgents(agents: List<Agent>) {
        context.agentStoreDataStore.edit { prefs ->
            prefs[Keys.CUSTOM_AGENTS_JSON] = encode(agents)
        }
    }

    suspend fun setActiveAgentId(id: String?) {
        context.agentStoreDataStore.edit { prefs ->
            if (id.isNullOrBlank()) {
                prefs.remove(Keys.ACTIVE_AGENT_ID)
            } else {
                prefs[Keys.ACTIVE_AGENT_ID] = id
            }
        }
    }

    private fun encode(agents: List<Agent>): String {
        val arr = JSONArray()
        agents.forEach { a ->
            arr.put(
                JSONObject()
                    .put("id", a.id)
                    .put("name", a.name)
                    .put("role", a.role)
                    .put("status", a.status)
                    .put("description", a.description)
                    .put("systemPrompt", a.systemPrompt)
                    .put("skills", JSONArray(a.skills))
                    .put("isBuiltIn", a.isBuiltIn)
                    .put("isActive", a.isActive)
                    .put("iconEmoji", a.iconEmoji)
                    .put("createdAt", a.createdAt)
            )
        }
        return arr.toString()
    }

    private fun decode(raw: String): List<Agent> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val skillsArr = obj.optJSONArray("skills")
                val skills = if (skillsArr != null) {
                    (0 until skillsArr.length()).map { skillsArr.getString(it) }
                } else emptyList()

                Agent(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    role = obj.optString("role", "Custom Agent"),
                    status = obj.optString("status", "Ready"),
                    description = obj.optString("description", ""),
                    systemPrompt = obj.optString("systemPrompt", ""),
                    skills = skills,
                    isBuiltIn = obj.optBoolean("isBuiltIn", false),
                    isActive = obj.optBoolean("isActive", false),
                    iconEmoji = obj.optString("iconEmoji", "\uD83E\uDD16"),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                )
            }
        }.getOrDefault(emptyList())
    }
}
