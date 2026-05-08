package com.mrrobot.aiworkspace.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val role: String,
    val content: String
)

class ChatRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

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

            val jsonMessages = JSONArray()

            jsonMessages.put(
                JSONObject()
                    .put("role", "system")
                    .put(
                        "content",
                        "You are Mr. Robot AI Workspace, a precise Android development assistant. Give useful, practical, production-ready answers."
                    )
            )

            messages.forEach { msg ->
                jsonMessages.put(
                    JSONObject()
                        .put("role", msg.role)
                        .put("content", msg.content)
                )
            }

            val bodyJson = JSONObject()
                .put("model", model.ifBlank { "openai/gpt-4o-mini" })
                .put("messages", jsonMessages)
                .put("temperature", 0.7)
                .put("max_tokens", 1200)

            val body = bodyJson
                .toString()
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .addHeader("HTTP-Referer", "https://github.com/devxvoid/Mr-Robot-AI-Workspace")
                .addHeader("X-Title", "Mr. Robot AI Workspace")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        IllegalStateException("OpenRouter error ${response.code}: $raw")
                    )
                }

                val content = JSONObject(raw)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                Result.success(content)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
