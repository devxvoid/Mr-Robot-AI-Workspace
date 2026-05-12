package com.mrrobot.aiworkspace.data

enum class ApiProvider(
    val displayName: String,
    val shortName: String,
    val keyLabel: String,
    val keyPlaceholder: String,
    val helpText: String
) {
    OpenRouter(
        displayName = "OpenRouter",
        shortName = "OR",
        keyLabel = "OpenRouter API Key",
        keyPlaceholder = "sk-or-v1-...",
        helpText = "One key for many hosted models."
    ),
    OpenAI(
        displayName = "OpenAI",
        shortName = "OpenAI",
        keyLabel = "OpenAI API Key",
        keyPlaceholder = "sk-...",
        helpText = "Use official OpenAI models directly."
    ),
    Anthropic(
        displayName = "Anthropic",
        shortName = "Claude",
        keyLabel = "Anthropic API Key",
        keyPlaceholder = "sk-ant-...",
        helpText = "Use Claude models directly."
    ),
    Gemini(
        displayName = "Google",
        shortName = "Gemini",
        keyLabel = "Gemini API Key",
        keyPlaceholder = "AIza...",
        helpText = "Use Google Gemini models directly."
    ),
    Groq(
        displayName = "Groq",
        shortName = "Groq",
        keyLabel = "Groq API Key",
        keyPlaceholder = "gsk_...",
        helpText = "Fast OpenAI-compatible inference."
    ),
    Mistral(
        displayName = "Mistral",
        shortName = "Mistral",
        keyLabel = "Mistral API Key",
        keyPlaceholder = "Enter Mistral API key",
        helpText = "Use Mistral and Codestral models."
    ),
    DeepSeek(
        displayName = "DeepSeek",
        shortName = "DeepSeek",
        keyLabel = "DeepSeek API Key",
        keyPlaceholder = "sk-...",
        helpText = "Use DeepSeek chat and reasoning models."
    ),
    XAI(
        displayName = "xAI",
        shortName = "Grok",
        keyLabel = "xAI API Key",
        keyPlaceholder = "xai-...",
        helpText = "Use Grok models directly."
    )
}

data class AiModel(
    val id: String,
    val name: String,
    val provider: String,
    val description: String,
    val apiProvider: ApiProvider
)

object AiModels {

    val supported = listOf(
        AiModel("openai/gpt-4o-mini", "GPT-4o Mini", "OpenRouter", "Fast general model through OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet", "OpenRouter", "Claude through OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-2.0-flash-001", "Gemini Flash", "OpenRouter", "Fast Gemini model through OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.1-70b-instruct", "Llama 3.1 70B", "OpenRouter", "Open model through OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-chat", "DeepSeek Chat", "OpenRouter", "DeepSeek through OpenRouter.", ApiProvider.OpenRouter),

        AiModel("gpt-4o-mini", "GPT-4o Mini", "OpenAI", "Fast official OpenAI model.", ApiProvider.OpenAI),
        AiModel("gpt-4o", "GPT-4o", "OpenAI", "Flagship OpenAI model.", ApiProvider.OpenAI),
        AiModel("o3-mini", "o3 Mini", "OpenAI", "Reasoning-focused OpenAI model.", ApiProvider.OpenAI),

        AiModel("claude-3-5-sonnet-latest", "Claude 3.5 Sonnet", "Anthropic", "Strong Claude model for coding and reasoning.", ApiProvider.Anthropic),
        AiModel("claude-3-5-haiku-latest", "Claude 3.5 Haiku", "Anthropic", "Fast Claude model for everyday tasks.", ApiProvider.Anthropic),

        AiModel("gemini-1.5-flash", "Gemini 1.5 Flash", "Google", "Fast Gemini model.", ApiProvider.Gemini),
        AiModel("gemini-1.5-pro", "Gemini 1.5 Pro", "Google", "Advanced Gemini model.", ApiProvider.Gemini),
        AiModel("gemini-2.0-flash", "Gemini 2.0 Flash", "Google", "Newer fast Gemini model.", ApiProvider.Gemini),

        AiModel("llama-3.3-70b-versatile", "Llama 3.3 70B", "Groq", "Fast Llama model on Groq.", ApiProvider.Groq),
        AiModel("mixtral-8x7b-32768", "Mixtral 8x7B", "Groq", "Fast Mixtral model on Groq.", ApiProvider.Groq),

        AiModel("mistral-large-latest", "Mistral Large", "Mistral", "Advanced Mistral model.", ApiProvider.Mistral),
        AiModel("codestral-latest", "Codestral", "Mistral", "Coding model from Mistral.", ApiProvider.Mistral),

        AiModel("deepseek-chat", "DeepSeek Chat", "DeepSeek", "DeepSeek general chat model.", ApiProvider.DeepSeek),
        AiModel("deepseek-reasoner", "DeepSeek Reasoner", "DeepSeek", "DeepSeek reasoning model.", ApiProvider.DeepSeek),

        AiModel("grok-2-latest", "Grok 2", "xAI", "xAI Grok model.", ApiProvider.XAI),
        AiModel("grok-beta", "Grok Beta", "xAI", "Beta Grok model.", ApiProvider.XAI)
    )

    fun byProvider(provider: ApiProvider): List<AiModel> {
        return supported.filter { it.apiProvider == provider }
    }

    fun defaultForProvider(provider: ApiProvider): AiModel {
        return byProvider(provider).firstOrNull() ?: supported.first()
    }

    fun byIdOrNull(id: String): AiModel? {
        return supported.firstOrNull { it.id == id }
    }

    fun findById(id: String): AiModel {
        return byIdOrNull(id) ?: supported.first()
    }
}
