package com.mrrobot.aiworkspace.data

data class ChatMessage(
    val role: String,
    val content: String
)

/**
 * The result of a chat send: the cleaned reply (after stripping any memory
 * directives the AI emitted) plus the list of memory keys that were saved or
 * forgotten as a side-effect.
 */
data class ChatReply(
    val text: String,
    val savedMemoryKeys: List<String> = emptyList(),
    val forgottenMemoryKeys: List<String> = emptyList()
) {
    val didMutateMemory: Boolean
        get() = savedMemoryKeys.isNotEmpty() || forgottenMemoryKeys.isNotEmpty()
}

/**
 * Repository for sending chat messages. Composes the system prompt from:
 *   1. Active agent (from [AgentStore])
 *   2. User's "soul" (from [AgentConfigStore])
 *   3. Memory tool instructions + persistent memories (from [MemoryStore])
 *
 * After the model replies, [MemoryDirectiveParser] silently extracts any
 * `[REMEMBER key = value]` or `[FORGET key]` directives the AI emitted and
 * persists them to [MemoryStore], returning the cleaned text in [ChatReply].
 */
class ChatRepository(
    private val agentConfigStore: AgentConfigStore? = null,
    private val memoryStore: MemoryStore? = null,
    private val agentStore: AgentStore? = null
) {

    private val directiveParser: MemoryDirectiveParser? =
        memoryStore?.let { MemoryDirectiveParser(it) }

    suspend fun sendMessage(
        settings: AppSettings,
        messages: List<ChatMessage>
    ): Result<ChatReply> {
        val systemPrompt = buildSystemPrompt()
        return runCatching {
            val raw = ProviderChatClient.generateReply(
                settings = settings,
                messages = messages,
                systemPrompt = systemPrompt
            )

            val parser = directiveParser
            if (parser != null) {
                val parsed = parser.applyDirectives(raw)
                ChatReply(
                    text = parsed.cleaned,
                    savedMemoryKeys = parsed.saved,
                    forgottenMemoryKeys = parsed.forgotten
                )
            } else {
                ChatReply(text = raw)
            }
        }
    }

    suspend fun sendMessage(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>
    ): Result<ChatReply> {
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
