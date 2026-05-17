package com.mrrobot.aiworkspace.data

/**
 * Composes the full chat system prompt: active agent + soul + memories.
 *
 * Layered prompt order:
 *   1. Active agent persona (if any agent is activated)
 *   2. Soul (user-customizable base persona)
 *   3. Memories grouped by category
 *
 * Format example:
 *   You are Android Architect.
 *   Mobile Architecture
 *
 *   Builds production-grade Android app structures...
 *
 *   ## Skills
 *   - Kotlin
 *   - Jetpack Compose
 *
 *   ---
 *
 *   <soul prompt>
 *
 *   ## Your Memories
 *   - **key**: content
 *
 *   ## User Preferences
 *   - **key**: content
 *
 *   ## Learnings (reinforced 5x)
 *   - **key**: content
 *
 *   ## Known Issues & Resolutions
 *   - **key**: content
 */
object SystemPromptBuilder {

    fun build(
        soul: SoulConfig,
        memories: List<MemoryEntry>,
        activeAgent: Agent? = null
    ): String = buildString {
        if (activeAgent != null) {
            append("You are ${activeAgent.name}.\n")
            append(activeAgent.role).append('\n')
            if (activeAgent.description.isNotBlank()) {
                append('\n').append(activeAgent.description).append('\n')
            }
            if (activeAgent.systemPrompt.isNotBlank()) {
                append('\n').append(activeAgent.systemPrompt).append('\n')
            }
            if (activeAgent.skills.isNotEmpty()) {
                append("\n## Skills\n")
                activeAgent.skills.forEach { append("- ").append(it).append('\n') }
            }
            append("\n---\n\n")
        }

        append(soul.effectivePrompt())

        val byCategory = memories.groupBy { it.category }

        appendCategory(
            header = "Your Memories",
            entries = byCategory[MemoryCategory.GENERAL].orEmpty(),
            withHitCount = false
        )
        appendCategory(
            header = "User Preferences",
            entries = byCategory[MemoryCategory.PREFERENCE].orEmpty(),
            withHitCount = false
        )
        appendCategory(
            header = "Learnings",
            entries = byCategory[MemoryCategory.LEARNING].orEmpty(),
            withHitCount = true
        )
        appendCategory(
            header = "Known Issues & Resolutions",
            entries = byCategory[MemoryCategory.ERROR].orEmpty(),
            withHitCount = false
        )
    }

    private fun StringBuilder.appendCategory(
        header: String,
        entries: List<MemoryEntry>,
        withHitCount: Boolean
    ) {
        if (entries.isEmpty()) return

        append("\n\n## ").append(header).append("\n")
        entries.forEach { entry ->
            append("- **").append(entry.key).append("**")
            if (withHitCount && entry.hitCount > 1) {
                append(" (reinforced ").append(entry.hitCount).append("x)")
            }
            append(": ").append(entry.content).append('\n')
        }
    }
}
