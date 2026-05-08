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

class OpenRouterRepository {

    private val client = OkHttpClient()

    suspend fun sendMessage(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>
    ): Result<String> {

        return withContext(Dispatchers.IO) {

            try {

                val jsonMessages = JSONArray()

                messages.forEach {

                    val item = JSONObject()

                    item.put("role", it.role)
                    item.put("content", it.content)

                    jsonMessages.put(item)
                }

                val body = JSONObject()

                body.put("model", model)
                body.put("messages", jsonMessages)

                val request = Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .addHeader(
                        "Authorization",
                        "Bearer $apiKey"
                    )
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .post(
                        body.toString()
                            .toRequestBody(
                                "application/json"
                                    .toMediaType()
                            )
                    )
                    .build()

                val response =
                    client.newCall(request).execute()

                val responseText =
                    response.body?.string().orEmpty()

                val json =
                    JSONObject(responseText)

                val content =
                    json
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                Result.success(content)

            } catch (e: Exception) {

                Result.failure(e)
            }
        }
    }
}
