package com.mrrobot.aiworkspace.data

data class AiModelOption(
    val id: String,
    val name: String,
    val provider: String,
    val description: String
)

object AiModels {
    val supported = listOf(
        AiModelOption(
            id = "openai/gpt-4o-mini",
            name = "GPT-4o Mini",
            provider = "OpenAI",
            description = "Fast, affordable, reliable general-purpose model."
        ),
        AiModelOption(
            id = "google/gemini-2.0-flash-001",
            name = "Gemini 2.0 Flash",
            provider = "Google",
            description = "Fast multimodal model with strong reasoning speed."
        ),
        AiModelOption(
            id = "anthropic/claude-3.5-sonnet",
            name = "Claude 3.5 Sonnet",
            provider = "Anthropic",
            description = "High-quality reasoning and writing model."
        ),
        AiModelOption(
            id = "deepseek/deepseek-chat",
            name = "DeepSeek Chat",
            provider = "DeepSeek",
            description = "Efficient chat and coding assistant."
        ),
        AiModelOption(
            id = "meta-llama/llama-3.1-8b-instruct",
            name = "Llama 3.1 8B Instruct",
            provider = "Meta",
            description = "Open-weight fast instruct model."
        ),
        AiModelOption(
            id = "mistralai/mistral-small",
            name = "Mistral Small",
            provider = "Mistral",
            description = "Balanced model for lightweight AI workflows."
        )
    )

    fun findById(id: String): AiModelOption {
        return supported.firstOrNull { it.id == id } ?: supported.first()
    }
}
