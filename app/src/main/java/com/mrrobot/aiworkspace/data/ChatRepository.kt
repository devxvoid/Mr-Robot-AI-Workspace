package com.mrrobot.aiworkspace.data

data class ChatMessage(
    val role: String,
    val content: String
)

/**
 * Repository for sending chat messages. Optionally composes the system prompt from
 * the user's "soul" + persistent memories before dispatching to [ProviderChatClient].
 *
 * Pass an [AgentConfigStore] + [MemoryStore] to enable persona / memory injection.
 * Without them the call falls back to a built-in default system prompt.
 */
class ChatRepository(
    private val agentConfigStore: AgentConfigStore? = null,
    private val memoryStore: MemoryStore? = null
) {

    suspend fun sendMessage(
        settings: AppSettings,
        messages: List<ChatMessage>
    ): Result<String> {
        val systemPrompt = buildSystemPrompt()
        return runCatching {
            ProviderChatClient.generateReply(
                settings = settings,
                messages = messages,
                systemPrompt = systemPrompt
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

    private suspend fun buildSystemPrompt(): String? {
        val soul = agentConfigStore?.getSoul() ?: return null
        val memories = memoryStore?.getAll().orEmpty()
        return SystemPromptBuilder.build(soul = soul, memories = memories)
    }
}
