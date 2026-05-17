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

        // Memory tools — taught to every model so natural-language
        // "remember my name is Alex" gets persisted automatically.
        append(MEMORY_TOOL_INSTRUCTIONS)

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

    private val MEMORY_TOOL_INSTRUCTIONS = """


## Memory Tools

You have persistent memory across conversations. When the user shares
information you should remember (their name, preferences, project
details, decisions, recurring facts, etc.), you MUST emit a memory
directive at the very end of your reply on its own line(s), in this
exact format:

[REMEMBER key = value]

Trigger this WHENEVER the user:
  - Tells you their name, location, role, or any personal fact
  - States a preference ("I prefer dark mode", "I always use Kotlin")
  - Says something like "remember that...", "don't forget...",
    "my name is...", "for next time..."
  - Decides on a recurring approach, convention, or rule

Use snake_case keys. Example exchanges:

User: "My name is Alex."
You: Nice to meet you, Alex! How can I help today?
[REMEMBER user_name = Alex]

User: "I'm building an Android app called Bolt."
You: Got it. What part of Bolt are we working on?
[REMEMBER current_project = Android app named Bolt]

User: "Always reply in formal English."
You: Understood. I will use formal English from now on.
[REMEMBER tone_preference = formal English]

To delete a memory, emit:

[FORGET key]

You can emit multiple directives, one per line. Directives are
hidden from the user — they are silently persisted. Do NOT mention
the directives in your visible reply, do NOT format them as code,
and do NOT explain them. Just emit them after your normal answer.

If the user explicitly asks "what do you remember about me?", read
the memories listed below and answer naturally.

""".trimIndent()
}
