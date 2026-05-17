package com.mrrobot.aiworkspace.data

/**
 * Composes the full chat system prompt: soul + memories.
 *
 * Format:
 *   <soul>
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
        memories: List<MemoryEntry>
    ): String = buildString {
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
