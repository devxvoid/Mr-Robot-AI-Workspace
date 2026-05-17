package com.mrrobot.aiworkspace.data

import java.util.Calendar

/**
 * Coordinates the autonomous heartbeat self-check.
 *
 * Responsibilities:
 *  - Decide when a heartbeat is due (interval + active hours window).
 *  - Build the heartbeat user-message prompt (config + pending memories + promotion candidates).
 *  - Record results to a rolling log.
 *  - Respond using the currently active provider/model in the user's settings.
 */
class HeartbeatManager(
    private val agentConfigStore: AgentConfigStore,
    private val memoryStore: MemoryStore,
    private val chatRepository: ChatRepository
) {

    /**
     * Returns true when the heartbeat is enabled, the current local hour is inside
     * the active window, and enough time has elapsed since the last run.
     */
    suspend fun isHeartbeatDue(now: Long = System.currentTimeMillis()): Boolean {
        val config = agentConfigStore.getHeartbeatConfig()
        if (!config.enabled) return false

        val cal = Calendar.getInstance().apply { timeInMillis = now }
        val hour = cal.get(Calendar.HOUR_OF_DAY)

        if (hour < config.activeHoursStart || hour >= config.activeHoursEnd) {
            return false
        }

        val elapsedMs = now - config.lastHeartbeatEpochMs
        val intervalMs = config.intervalMinutes * 60_000L
        return elapsedMs >= intervalMs
    }

    /** Builds the user-message body sent to the model when the heartbeat fires. */
    suspend fun buildHeartbeatPrompt(): String {
        val config = agentConfigStore.getHeartbeatConfig()
        val memories = memoryStore.getAll()
        val candidates = memories.filter { it.hitCount >= 5 }
        val recent = agentConfigStore.getHeartbeatLog().take(3)

        return buildString {
            append(config.effectivePrompt())
            append('\n')

            if (recent.isNotEmpty()) {
                append("\n## Previous Heartbeat Results\n")
                recent.forEachIndexed { i, e ->
                    append(i + 1).append(". ")
                    append(if (e.success) "ok" else "error")
                    e.response?.takeIf { it.isNotBlank() }?.let {
                        append(" — ").append(it.take(200))
                    }
                    e.error?.takeIf { it.isNotBlank() }?.let {
                        append(" — error: ").append(it.take(160))
                    }
                    append('\n')
                }
            }

            if (memories.isNotEmpty()) {
                append("\n## Memory Snapshot\n")
                append("You have ").append(memories.size).append(" stored memories.\n")
            }

            if (candidates.isNotEmpty()) {
                append("\n## Promotion Candidates\n")
                append("These memories have been reinforced 5+ times and could be promoted into your soul/system prompt:\n")
                candidates.forEach { entry ->
                    append("- **").append(entry.key)
                    append("** (hits: ").append(entry.hitCount)
                    append(", category: ").append(entry.category.name).append("): ")
                    append(entry.content).append('\n')
                }
            }
        }
    }

    /**
     * Runs one heartbeat against the current settings. Records the outcome and updates
     * the last-run timestamp. Safe to call from a background coroutine.
     */
    suspend fun runHeartbeat(settings: AppSettings): HeartbeatLogEntry {
        val now = System.currentTimeMillis()
        val prompt = buildHeartbeatPrompt()

        val result = chatRepository.sendMessage(
            settings = settings,
            messages = listOf(ChatMessage(role = "user", content = prompt))
        )

        val entry = result.fold(
            onSuccess = { reply ->
                HeartbeatLogEntry(
                    timestampEpochMs = now,
                    success = true,
                    response = reply.trim().takeIf { it.isNotBlank() }
                )
            },
            onFailure = { err ->
                HeartbeatLogEntry(
                    timestampEpochMs = now,
                    success = false,
                    error = err.message ?: err::class.simpleName
                )
            }
        )

        agentConfigStore.appendHeartbeatLog(entry)
        agentConfigStore.markHeartbeatRun(now)
        return entry
    }
}
