package com.mrrobot.aiworkspace.data

/**
 * Extracts `[SEARCH ...]` directives from an AI reply. Used by
 * [ChatRepository] to power the tool-calling loop for real-time data.
 *
 * Format the AI emits:
 *   [SEARCH latest iPhone model 2026]
 *
 * Multiple directives in one reply are supported; each runs in parallel
 * up to a sane cap.
 */
object SearchDirectiveParser {

    private val SEARCH_REGEX = Regex(
        pattern = """\[\s*SEARCH\s+([^\]]+?)\s*]""",
        options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    )

    /** Return all queries the AI asked us to run. */
    fun extractQueries(reply: String): List<String> {
        if (!reply.contains('[')) return emptyList()
        return SEARCH_REGEX.findAll(reply)
            .mapNotNull { match ->
                match.groupValues[1].trim().trim('"', '\'').takeIf { it.isNotBlank() }
            }
            .distinct()
            .toList()
    }

    /** Strip directives so the AI's intermediate "let me search" text isn't shown. */
    fun stripDirectives(reply: String): String {
        return reply.replace(SEARCH_REGEX, "").trim()
    }
}
