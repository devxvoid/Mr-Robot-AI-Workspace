package com.mrrobot.aiworkspace.ai

import com.mrrobot.aiworkspace.data.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OpenRouterRepository {

    private val client =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

    suspend fun sendMessage(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val jsonMessages = JSONArray()

                messages.forEach { msg ->
                    jsonMessages.put(
                        JSONObject()
                            .put("role", msg.role)
                            .put("content", msg.content)
                    )
                }

                val body = JSONObject()
                    .put("model", model.ifBlank { "openai/gpt-4o-mini" })
                    .put("messages", jsonMessages)
                    .put("temperature", 0.7)

                val request = Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .addHeader("Authorization", "Bearer ${apiKey.trim()}")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "https://github.com/devxvoid/Mr-Robot-AI-Workspace")
                    .addHeader("X-Title", "Mr. Robot AI Workspace")
                    .post(
                        body.toString()
                            .toRequestBody("application/json".toMediaType())
                    )
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseText = response.body?.string().orEmpty()

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("OpenRouter error ${response.code}: $responseText")
                        )
                    }

                    val json = JSONObject(responseText)

                    val content =
                        json.getJSONArray("choices")
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
}
