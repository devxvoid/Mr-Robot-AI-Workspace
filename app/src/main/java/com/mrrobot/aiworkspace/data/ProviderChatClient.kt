package com.mrrobot.aiworkspace.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ProviderChatClient {

    suspend fun generateReply(
        settings: AppSettings,
        messages: List<ChatMessage>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = settings.activeApiKey()
        val provider = settings.selectedProvider
        val model = settings.activeModel()

        if (apiKey.isBlank()) {
            throw IllegalStateException("No active AI model. Open Settings, add an API key, then tap Save & Activate.")
        }

        when (provider) {
            ApiProvider.OpenRouter -> openAiCompatible(
                url = "https://openrouter.ai/api/v1/chat/completions",
                apiKey = apiKey,
                model = model,
                messages = messages,
                extraHeaders = mapOf(
                    "HTTP-Referer" to "https://github.com/devxvoid/Mr-Robot-AI-Workspace",
                    "X-Title" to "Mr. Robot AI Workspace"
                )
            )

            ApiProvider.OpenAI -> openAiCompatible(
                url = "https://api.openai.com/v1/chat/completions",
                apiKey = apiKey,
                model = model,
                messages = messages
            )

            ApiProvider.Groq -> openAiCompatible(
                url = "https://api.groq.com/openai/v1/chat/completions",
                apiKey = apiKey,
                model = model,
                messages = messages
            )

            ApiProvider.Mistral -> openAiCompatible(
                url = "https://api.mistral.ai/v1/chat/completions",
                apiKey = apiKey,
                model = model,
                messages = messages
            )

            ApiProvider.DeepSeek -> openAiCompatible(
                url = "https://api.deepseek.com/chat/completions",
                apiKey = apiKey,
                model = model,
                messages = messages
            )

            ApiProvider.XAI -> openAiCompatible(
                url = "https://api.x.ai/v1/chat/completions",
                apiKey = apiKey,
                model = model,
                messages = messages
            )

            ApiProvider.Anthropic -> anthropic(
                apiKey = apiKey,
                model = model,
                messages = messages
            )

            ApiProvider.Gemini -> gemini(
                apiKey = apiKey,
                model = model,
                messages = messages
            )
        }
    }

    private fun openAiCompatible(
        url: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        extraHeaders: Map<String, String> = emptyMap()
    ): String {
        val jsonMessages = JSONArray()

        jsonMessages.put(
            JSONObject()
                .put("role", "system")
                .put("content", "You are ALPHA inside Mr. Robot AI Workspace. Be precise, practical, and helpful.")
        )

        messages.forEach { message ->
            jsonMessages.put(
                JSONObject()
                    .put("role", message.role)
                    .put("content", message.content)
            )
        }

        val payload = JSONObject()
            .put("model", model)
            .put("messages", jsonMessages)
            .put("temperature", 0.7)

        val response = postJson(
            url = url,
            body = payload,
            headers = mapOf(
                "Authorization" to "Bearer $apiKey",
                "Content-Type" to "application/json"
            ) + extraHeaders
        )

        return JSONObject(response)
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim()
    }

    private fun anthropic(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>
    ): String {
        val anthropicMessages = JSONArray()

        messages
            .filter { it.role == "user" || it.role == "assistant" }
            .forEach { message ->
                anthropicMessages.put(
                    JSONObject()
                        .put("role", message.role)
                        .put("content", message.content)
                )
            }

        val payload = JSONObject()
            .put("model", model)
            .put("max_tokens", 1200)
            .put("messages", anthropicMessages)

        val response = postJson(
            url = "https://api.anthropic.com/v1/messages",
            body = payload,
            headers = mapOf(
                "x-api-key" to apiKey,
                "anthropic-version" to "2023-06-01",
                "Content-Type" to "application/json"
            )
        )

        return JSONObject(response)
            .getJSONArray("content")
            .getJSONObject(0)
            .getString("text")
            .trim()
    }

    private fun gemini(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>
    ): String {
        val prompt = messages.joinToString(separator = "\n\n") { message ->
            "${message.role.uppercase()}: ${message.content}"
        }

        val endpoint =
            "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=" +
                URLEncoder.encode(apiKey, "UTF-8")

        val payload = JSONObject()
            .put(
                "contents",
                JSONArray()
                    .put(
                        JSONObject()
                            .put(
                                "parts",
                                JSONArray()
                                    .put(JSONObject().put("text", prompt))
                            )
                    )
            )

        val response = postJson(
            url = endpoint,
            body = payload,
            headers = mapOf(
                "Content-Type" to "application/json"
            )
        )

        return JSONObject(response)
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
            .trim()
    }

    private fun postJson(
        url: String,
        body: JSONObject,
        headers: Map<String, String>
    ): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 30000
            readTimeout = 90000
            doOutput = true
            headers.forEach { (key, value) ->
                setRequestProperty(key, value)
            }
        }

        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(body.toString())
            writer.flush()
        }

        val status = connection.responseCode
        val stream = if (status in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        }

        val response = BufferedReader(InputStreamReader(stream)).use { reader ->
            reader.readText()
        }

        connection.disconnect()

        if (status !in 200..299) {
            throw IllegalStateException("AI request failed ($status): $response")
        }

        return response
    }
}
