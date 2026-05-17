package com.mrrobot.aiworkspace.data

data class ChatMessage(
    val role: String,
    val content: String
)

/**
 * Repository for sending chat messages. Composes the system prompt from:
 *   1. Active agent (from [AgentStore])
 *   2. User's "soul" (from [AgentConfigStore])
 *   3. Persistent memories (from [MemoryStore])
 *
 * Pass [agentConfigStore], [memoryStore], and [agentStore] to enable the full
 * brain. Without them, the call falls back to a built-in default system prompt.
 */
class ChatRepository(
    private val agentConfigStore: AgentConfigStore? = null,
    private val memoryStore: MemoryStore? = null,
    private val agentStore: AgentStore? = null
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

    /**
     * Build the composed system prompt. Returns null if no stores are wired
     * (caller falls back to ProviderChatClient's built-in default).
     */
    suspend fun buildSystemPrompt(): String? {
        val soul = agentConfigStore?.getSoul() ?: return null
        val memories = memoryStore?.getAll().orEmpty()
        val activeAgent = agentStore?.getActiveAgent()
        return SystemPromptBuilder.build(
            soul = soul,
            memories = memories,
            activeAgent = activeAgent
        )
    }
}
