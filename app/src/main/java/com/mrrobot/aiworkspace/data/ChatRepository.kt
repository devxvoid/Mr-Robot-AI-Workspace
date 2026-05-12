package com.mrrobot.aiworkspace.data

data class ChatMessage(
    val role: String,
    val content: String
)

class ChatRepository {

    suspend fun sendMessage(
        settings: AppSettings,
        messages: List<ChatMessage>
    ): Result<String> {
        return runCatching {
            ProviderChatClient.generateReply(
                settings = settings,
                messages = messages
            )
        }
    }

    suspend fun sendMessage(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>
    ): Result<String> {
        val settings = AppSettings(
            apiKey = apiKey,
            model = model,
            selectedProvider = ApiProvider.OpenRouter,
            openRouterApiKey = apiKey,
            openRouterModel = model
        )

        return sendMessage(
            settings = settings,
            messages = messages
        )
    }
}
