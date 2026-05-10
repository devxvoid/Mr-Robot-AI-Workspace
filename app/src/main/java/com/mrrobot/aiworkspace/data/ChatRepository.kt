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

data class ChatMessage(
    val role: String,
    val content: String,
    val imageDataUrls: List<String> = emptyList()
)

class ChatRepository {

    suspend fun sendMessage(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException("OpenRouter API key is missing. Add it in Settings.")
                )
            }

            val requestJson = buildRequestJson(
                model = model.ifBlank { "openai/gpt-4o-mini" },
                messages = messages
            )

            val url = URL("https://openrouter.ai/api/v1/chat/completions")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.connectTimeout = 30_000
            connection.readTimeout = 120_000
            connection.doOutput = true
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("HTTP-Referer", "https://github.com/devxvoid/Mr-Robot-AI-Workspace")
            connection.setRequestProperty("X-Title", "Mr. Robot AI Workspace")

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestJson.toString())
                writer.flush()
            }

            val statusCode = connection.responseCode

            val responseText = if (statusCode in 200..299) {
                readStream(connection.inputStream.reader())
            } else {
                val errorStream = connection.errorStream
                if (errorStream != null) {
                    readStream(errorStream.reader())
                } else {
                    "OpenRouter request failed with status $statusCode"
                }
            }

            connection.disconnect()

            if (statusCode !in 200..299) {
                return@withContext Result.failure(
                    IllegalStateException("OpenRouter error $statusCode: $responseText")
                )
            }

            val reply = parseAssistantReply(responseText)

            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildRequestJson(
        model: String,
        messages: List<ChatMessage>
    ): JSONObject {
        val jsonMessages = JSONArray()

        jsonMessages.put(
            JSONObject()
                .put("role", "system")
                .put(
                    "content",
                    "You are Mr. Robot AI Workspace, a precise Android development assistant. When the user attaches an image, analyze the actual image content, not only the file metadata. Describe visible UI, layout, errors, objects, text, design problems, and useful improvements."
                )
        )

        messages.forEach { message ->
            val jsonMessage = JSONObject()
                .put("role", message.role)

            if (message.imageDataUrls.isNotEmpty()) {
                val contentArray = JSONArray()

                contentArray.put(
                    JSONObject()
                        .put("type", "text")
                        .put("text", message.content)
                )

                message.imageDataUrls.forEach { imageDataUrl ->
                    contentArray.put(
                        JSONObject()
                            .put("type", "image_url")
                            .put(
                                "image_url",
                                JSONObject().put("url", imageDataUrl)
                            )
                    )
                }

                jsonMessage.put("content", contentArray)
            } else {
                jsonMessage.put("content", message.content)
            }

            jsonMessages.put(jsonMessage)
        }

        return JSONObject()
            .put("model", model)
            .put("messages", jsonMessages)
            .put("temperature", 0.55)
            .put("max_tokens", 1600)
    }

    private fun parseAssistantReply(responseText: String): String {
        val root = JSONObject(responseText)
        val choices = root.optJSONArray("choices")

        if (choices == null || choices.length() == 0) {
            return "No response returned from OpenRouter."
        }

        val first = choices.getJSONObject(0)
        val message = first.optJSONObject("message")

        return message
            ?.optString("content")
            ?.takeIf { it.isNotBlank() }
            ?: "Empty response returned from OpenRouter."
    }

    private fun readStream(reader: InputStreamReader): String {
        return BufferedReader(reader).use { buffered ->
            buildString {
                while (true) {
                    val line = buffered.readLine() ?: break
                    append(line)
                    append('\n')
                }
            }
        }
    }
}
