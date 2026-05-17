package com.mrrobot.aiworkspace.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * A simple, key-less web-search tool backed by DuckDuckGo's public HTML SERP
 * (`https://html.duckduckgo.com/html/?q=...`). Returns up to [maxResults]
 * organic results, each with a title, URL, and snippet.
 *
 * Used by [ChatRepository] when the AI emits a `[SEARCH ...]` directive.
 * Failure modes (network error, blocked, parse failure) are returned as an
 * empty list rather than throwing — the model handles "no results" gracefully.
 */
object WebSearchTool {

    data class Result(
        val title: String,
        val url: String,
        val snippet: String
    )

    private const val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/120.0.0.0 Mobile Safari/537.36"

    suspend fun search(query: String, maxResults: Int = 5): List<Result> =
        withContext(Dispatchers.IO) {
            if (query.isBlank()) return@withContext emptyList()

            val encoded = URLEncoder.encode(query.trim(), "UTF-8")
            val url = "https://html.duckduckgo.com/html/?q=$encoded"

            val html = runCatching { fetchHtml(url) }.getOrNull()
                ?: return@withContext emptyList()

            parseDuckDuckGoHtml(html, maxResults)
        }

    private fun fetchHtml(url: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 20000
            instanceFollowRedirects = true
            setRequestProperty("User-Agent", USER_AGENT)
            setRequestProperty(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
            )
            setRequestProperty("Accept-Language", "en-US,en;q=0.9")
        }

        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
        connection.disconnect()

        if (status !in 200..299) error("Search request failed with status $status")
        return body
    }

    private fun parseDuckDuckGoHtml(html: String, maxResults: Int): List<Result> {
        val results = mutableListOf<Result>()
        val resultRegex = Regex(
            pattern = """<a[^>]*class="result__a"[^>]*href="([^"]+)"[^>]*>(.*?)</a>(?:.*?<a[^>]*class="result__snippet"[^>]*>(.*?)</a>)?""",
            options = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
        )

        for (match in resultRegex.findAll(html)) {
            if (results.size >= maxResults) break
            val rawUrl = match.groupValues[1]
            val rawTitle = match.groupValues[2]
            val rawSnippet = match.groupValues.getOrNull(3).orEmpty()

            val url = decodeDuckDuckGoUrl(rawUrl)
            val title = stripHtml(rawTitle)
            val snippet = stripHtml(rawSnippet)

            if (title.isNotBlank() && url.isNotBlank()) {
                results.add(
                    Result(
                        title = title,
                        url = url,
                        snippet = snippet
                    )
                )
            }
        }
        return results
    }

    /**
     * DuckDuckGo HTML wraps target URLs as `/l/?uddg=ENCODED_URL&...`. Unwrap
     * to get the actual destination so we can show clean links to the model.
     */
    private fun decodeDuckDuckGoUrl(href: String): String {
        val marker = "uddg="
        val idx = href.indexOf(marker)
        if (idx < 0) {
            return if (href.startsWith("//")) "https:$href" else href
        }
        val tail = href.substring(idx + marker.length)
        val end = tail.indexOf('&').let { if (it == -1) tail.length else it }
        val encoded = tail.substring(0, end)
        return runCatching { java.net.URLDecoder.decode(encoded, "UTF-8") }
            .getOrDefault(encoded)
    }

    private fun stripHtml(raw: String): String {
        if (raw.isBlank()) return ""
        return raw
            .replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#x27;", "'")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /** Format a list of results as a markdown block to feed back to the model. */
    fun formatForPrompt(query: String, results: List<Result>): String {
        if (results.isEmpty()) {
            return "Search results for \"$query\": no results found."
        }
        return buildString {
            append("Search results for \"").append(query).append("\":\n")
            results.forEachIndexed { i, r ->
                append(i + 1).append(". **").append(r.title).append("**\n")
                append("   ").append(r.url).append('\n')
                if (r.snippet.isNotBlank()) {
                    append("   ").append(r.snippet).append('\n')
                }
            }
        }
    }
}
