package com.mrrobot.aiworkspace.data

/**
 * Extracts and applies in-line memory directives the AI emits in its replies.
 *
 * The model is instructed (via [SystemPromptBuilder]) to append directives like:
 *
 *   [REMEMBER user_name = Alex]
 *   [FORGET stale_key]
 *
 * after its natural-language reply. This parser pulls those out, applies them
 * to the [MemoryStore], and returns:
 *  - the cleaned reply text (with directives stripped)
 *  - the list of saved/forgotten keys, so the UI can show a confirmation
 */
class MemoryDirectiveParser(private val memoryStore: MemoryStore) {

    data class Result(
        val cleaned: String,
        val saved: List<String> = emptyList(),
        val forgotten: List<String> = emptyList()
    ) {
        val hasChanges: Boolean
            get() = saved.isNotEmpty() || forgotten.isNotEmpty()
    }

    suspend fun applyDirectives(reply: String): Result {
        if (!reply.contains('[')) return Result(cleaned = reply)

        val saved = mutableListOf<String>()
        val forgotten = mutableListOf<String>()

        REMEMBER_REGEX.findAll(reply).forEach { match ->
            val key = match.groupValues[1].trim().sanitizeKey()
            val content = match.groupValues[2].trim().trim('"', '\'').trim()
            if (key.isNotBlank() && content.isNotBlank()) {
                runCatching {
                    memoryStore.store(
                        key = key,
                        content = content,
                        category = MemoryCategory.GENERAL,
                        source = "ai-auto"
                    )
                    saved.add(key)
                }
            }
        }

        FORGET_REGEX.findAll(reply).forEach { match ->
            val key = match.groupValues[1].trim().sanitizeKey()
            if (key.isNotBlank()) {
                runCatching {
                    val ok = memoryStore.forget(key)
                    if (ok) forgotten.add(key)
                }
            }
        }

        // Strip directives and any trailing whitespace they leave behind.
        val cleaned = reply
            .replace(REMEMBER_REGEX, "")
            .replace(FORGET_REGEX, "")
            .lines()
            .joinToString("\n") { it.trimEnd() }
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

        return Result(
            cleaned = cleaned.ifBlank { reply.trim() },
            saved = saved,
            forgotten = forgotten
        )
    }

    private fun String.sanitizeKey(): String =
        this.replace(Regex("[^A-Za-z0-9_\\- ]"), "")
            .trim()
            .replace(Regex("\\s+"), "_")
            .lowercase()

    companion object {
        // [REMEMBER key = value] — case-insensitive
        private val REMEMBER_REGEX = Regex(
            pattern = """\[\s*REMEMBER\s+([^=\]]+?)\s*=\s*([^\]]+?)\s*]""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        )

        // [FORGET key]
        private val FORGET_REGEX = Regex(
            pattern = """\[\s*FORGET\s+([^\]]+?)\s*]""",
            options = setOf(RegexOption.IGNORE_CASE)
        )
    }
}
