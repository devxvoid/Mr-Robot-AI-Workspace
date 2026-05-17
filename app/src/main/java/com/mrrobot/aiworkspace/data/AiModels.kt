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
        helpText = "One key, hundreds of models from every major lab."
    ),
    OpenAI(
        displayName = "OpenAI",
        shortName = "OpenAI",
        keyLabel = "OpenAI API Key",
        keyPlaceholder = "sk-...",
        helpText = "Use official OpenAI models directly."
    ),
    Anthropic(
        displayName = "Anthropic Claude",
        shortName = "Claude",
        keyLabel = "Anthropic API Key",
        keyPlaceholder = "sk-ant-...",
        helpText = "Use Claude models directly."
    ),
    Gemini(
        displayName = "Google Gemini",
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
        helpText = "Ultra-fast OpenAI-compatible Groq inference."
    ),
    Mistral(
        displayName = "Mistral",
        shortName = "Mistral",
        keyLabel = "Mistral API Key",
        keyPlaceholder = "Enter Mistral API key",
        helpText = "Use Mistral, Codestral, Pixtral and Magistral models."
    ),
    DeepSeek(
        displayName = "DeepSeek",
        shortName = "DeepSeek",
        keyLabel = "DeepSeek API Key",
        keyPlaceholder = "sk-...",
        helpText = "Use DeepSeek chat and reasoning models."
    ),
    XAI(
        displayName = "xAI Grok",
        shortName = "Grok",
        keyLabel = "xAI API Key",
        keyPlaceholder = "xai-...",
        helpText = "Use Grok models directly."
    ),
    Cohere(
        displayName = "Cohere",
        shortName = "Cohere",
        keyLabel = "Cohere API Key",
        keyPlaceholder = "Enter Cohere API key",
        helpText = "Use Cohere Command models via OpenAI-compatible API."
    ),
    Perplexity(
        displayName = "Perplexity",
        shortName = "PPLX",
        keyLabel = "Perplexity API Key",
        keyPlaceholder = "pplx-...",
        helpText = "Use Perplexity Sonar search-grounded models."
    ),
    Together(
        displayName = "Together AI",
        shortName = "Together",
        keyLabel = "Together API Key",
        keyPlaceholder = "Enter Together API key",
        helpText = "Run hundreds of open models on Together AI."
    ),
    Fireworks(
        displayName = "Fireworks AI",
        shortName = "Fireworks",
        keyLabel = "Fireworks API Key",
        keyPlaceholder = "fw_...",
        helpText = "Fast open model inference on Fireworks AI."
    ),
    Moonshot(
        displayName = "Moonshot Kimi",
        shortName = "Kimi",
        keyLabel = "Moonshot API Key",
        keyPlaceholder = "sk-...",
        helpText = "Use Moonshot AI Kimi models directly."
    ),
    ZAI(
        displayName = "Z.ai GLM",
        shortName = "GLM",
        keyLabel = "Z.ai API Key",
        keyPlaceholder = "Enter Z.ai API key",
        helpText = "Use Z.ai GLM models directly."
    ),
    NvidiaNim(
        displayName = "NVIDIA NIM",
        shortName = "NIM",
        keyLabel = "NVIDIA API Key",
        keyPlaceholder = "nvapi-...",
        helpText = "Use NVIDIA NIM hosted models (build.nvidia.com)."
    ),
    HuggingFace(
        displayName = "Hugging Face",
        shortName = "HF",
        keyLabel = "Hugging Face Token",
        keyPlaceholder = "hf_...",
        helpText = "Use Hugging Face Inference Providers (router.huggingface.co)."
    )
}

data class AiModel(
    val id: String,
    val name: String,
    val provider: String,
    val description: String,
    val apiProvider: ApiProvider
)

typealias AiModelOption = AiModel

data class ProviderModelConfig(
    val provider: ApiProvider,
    val modelId: String,
    val apiKey: String
) {
    val hasKey: Boolean
        get() = apiKey.isNotBlank()
}

object AiModels {
    val supported = listOf(
        // ──────────────────────────────────────────────────────────────
        //  OpenRouter — unified gateway to hundreds of frontier models
        // ──────────────────────────────────────────────────────────────

        // OpenAI via OpenRouter
        AiModel("openai/gpt-5", "GPT-5", "OpenRouter", "OpenAI flagship via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-5-mini", "GPT-5 Mini", "OpenRouter", "Compact GPT-5 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-5-nano", "GPT-5 Nano", "OpenRouter", "Ultra-light GPT-5 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-4.1", "GPT-4.1", "OpenRouter", "Long-context GPT-4.1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-4.1-mini", "GPT-4.1 Mini", "OpenRouter", "Affordable GPT-4.1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-4.1-nano", "GPT-4.1 Nano", "OpenRouter", "Tiny GPT-4.1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-4o", "GPT-4o", "OpenRouter", "OpenAI omni model via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-4o-mini", "GPT-4o Mini", "OpenRouter", "Fast GPT-4o via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/o3", "o3", "OpenRouter", "OpenAI o3 reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/o3-mini", "o3 Mini", "OpenRouter", "Cheaper o3 reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/o4-mini", "o4 Mini", "OpenRouter", "Newer o4 reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/o1", "o1", "OpenRouter", "Original o1 reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/o1-mini", "o1 Mini", "OpenRouter", "Compact o1 reasoning via OpenRouter.", ApiProvider.OpenRouter),

        // Anthropic via OpenRouter
        AiModel("anthropic/claude-opus-4.1", "Claude Opus 4.1", "OpenRouter", "Top-tier Claude Opus via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-opus-4", "Claude Opus 4", "OpenRouter", "Claude Opus 4 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-sonnet-4.5", "Claude Sonnet 4.5", "OpenRouter", "Latest Sonnet via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-sonnet-4", "Claude Sonnet 4", "OpenRouter", "Claude Sonnet 4 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-haiku-4.5", "Claude Haiku 4.5", "OpenRouter", "Fast Haiku via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-3.7-sonnet", "Claude 3.7 Sonnet", "OpenRouter", "Claude 3.7 Sonnet via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet", "OpenRouter", "Claude 3.5 Sonnet via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-3.5-haiku", "Claude 3.5 Haiku", "OpenRouter", "Claude 3.5 Haiku via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-3-opus", "Claude 3 Opus", "OpenRouter", "Legacy Opus via OpenRouter.", ApiProvider.OpenRouter),

        // Google via OpenRouter
        AiModel("google/gemini-2.5-pro", "Gemini 2.5 Pro", "OpenRouter", "Top Gemini reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-2.5-flash", "Gemini 2.5 Flash", "OpenRouter", "Fast Gemini 2.5 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-2.5-flash-lite", "Gemini 2.5 Flash Lite", "OpenRouter", "Lightweight Gemini via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-2.0-flash-001", "Gemini 2.0 Flash", "OpenRouter", "Gemini 2.0 Flash via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-2.0-flash-lite-001", "Gemini 2.0 Flash Lite", "OpenRouter", "Tiny Gemini 2.0 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-flash-1.5", "Gemini 1.5 Flash", "OpenRouter", "Gemini 1.5 Flash via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-pro-1.5", "Gemini 1.5 Pro", "OpenRouter", "Gemini 1.5 Pro via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemma-3-27b-it", "Gemma 3 27B", "OpenRouter", "Open Gemma 3 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemma-2-27b-it", "Gemma 2 27B", "OpenRouter", "Open Gemma 2 via OpenRouter.", ApiProvider.OpenRouter),

        // Meta Llama via OpenRouter
        AiModel("meta-llama/llama-4-maverick", "Llama 4 Maverick", "OpenRouter", "Meta flagship via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-4-scout", "Llama 4 Scout", "OpenRouter", "Compact Llama 4 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.3-70b-instruct", "Llama 3.3 70B", "OpenRouter", "Llama 3.3 70B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.1-405b-instruct", "Llama 3.1 405B", "OpenRouter", "Largest Llama 3.1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.1-70b-instruct", "Llama 3.1 70B", "OpenRouter", "Llama 3.1 70B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.1-8b-instruct", "Llama 3.1 8B", "OpenRouter", "Lightweight Llama via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.2-90b-vision-instruct", "Llama 3.2 90B Vision", "OpenRouter", "Vision Llama via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.2-11b-vision-instruct", "Llama 3.2 11B Vision", "OpenRouter", "Compact vision Llama via OpenRouter.", ApiProvider.OpenRouter),

        // DeepSeek via OpenRouter
        AiModel("deepseek/deepseek-chat", "DeepSeek V3 Chat", "OpenRouter", "DeepSeek chat via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-r1", "DeepSeek R1", "OpenRouter", "DeepSeek reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-r1-distill-llama-70b", "DeepSeek R1 Distill 70B", "OpenRouter", "Distilled R1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-coder", "DeepSeek Coder", "OpenRouter", "Code-focused DeepSeek via OpenRouter.", ApiProvider.OpenRouter),

        // Qwen via OpenRouter
        AiModel("qwen/qwen3-235b-a22b", "Qwen3 235B", "OpenRouter", "Alibaba Qwen3 flagship via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen-2.5-72b-instruct", "Qwen 2.5 72B", "OpenRouter", "Qwen 2.5 72B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen-2.5-32b-instruct", "Qwen 2.5 32B", "OpenRouter", "Qwen 2.5 32B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen-2.5-coder-32b-instruct", "Qwen 2.5 Coder 32B", "OpenRouter", "Code Qwen via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwq-32b", "Qwen QwQ 32B", "OpenRouter", "Reasoning Qwen via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen-vl-max", "Qwen VL Max", "OpenRouter", "Qwen vision via OpenRouter.", ApiProvider.OpenRouter),

        // Mistral via OpenRouter
        AiModel("mistralai/mistral-large", "Mistral Large", "OpenRouter", "Mistral Large via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/mistral-medium-3", "Mistral Medium 3", "OpenRouter", "Mistral Medium via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/mistral-small-3.2-24b-instruct", "Mistral Small 3.2", "OpenRouter", "Mistral Small via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/codestral-2501", "Codestral 25.01", "OpenRouter", "Mistral coding model via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/pixtral-large-2411", "Pixtral Large", "OpenRouter", "Mistral vision via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/ministral-8b", "Ministral 8B", "OpenRouter", "Edge-size Mistral via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/mixtral-8x22b-instruct", "Mixtral 8x22B", "OpenRouter", "Sparse MoE Mixtral via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/mixtral-8x7b-instruct", "Mixtral 8x7B", "OpenRouter", "Classic Mixtral via OpenRouter.", ApiProvider.OpenRouter),

        // xAI via OpenRouter
        AiModel("x-ai/grok-4", "Grok 4", "OpenRouter", "xAI Grok 4 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("x-ai/grok-4-fast", "Grok 4 Fast", "OpenRouter", "Cheap Grok 4 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("x-ai/grok-code-fast-1", "Grok Code Fast 1", "OpenRouter", "Code Grok via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("x-ai/grok-3", "Grok 3", "OpenRouter", "xAI Grok 3 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("x-ai/grok-3-mini", "Grok 3 Mini", "OpenRouter", "Cheaper Grok via OpenRouter.", ApiProvider.OpenRouter),

        // Other notable providers via OpenRouter
        AiModel("nvidia/nemotron-nano-9b-v2", "Nemotron Nano 9B v2", "OpenRouter", "NVIDIA Nemotron via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("nousresearch/hermes-4-405b", "Hermes 4 405B", "OpenRouter", "Nous Hermes 4 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("nousresearch/hermes-4-70b", "Hermes 4 70B", "OpenRouter", "Compact Hermes via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("cohere/command-r-plus", "Cohere Command R+", "OpenRouter", "Cohere flagship via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("cohere/command-r", "Cohere Command R", "OpenRouter", "Cohere Command R via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("amazon/nova-pro-v1", "Amazon Nova Pro", "OpenRouter", "Amazon Nova Pro via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("amazon/nova-lite-v1", "Amazon Nova Lite", "OpenRouter", "Amazon Nova Lite via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("amazon/nova-micro-v1", "Amazon Nova Micro", "OpenRouter", "Tiny Amazon Nova via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("microsoft/phi-4", "Microsoft Phi-4", "OpenRouter", "MSFT Phi-4 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("microsoft/phi-4-multimodal-instruct", "Phi-4 Multimodal", "OpenRouter", "MSFT Phi-4 vision via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("perplexity/sonar-pro", "Perplexity Sonar Pro", "OpenRouter", "Search-enhanced via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("perplexity/sonar", "Perplexity Sonar", "OpenRouter", "Web-grounded via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("moonshotai/kimi-k2", "Moonshot Kimi K2", "OpenRouter", "Kimi long-context via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("inflection/inflection-3-pi", "Inflection Pi", "OpenRouter", "Inflection Pi via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("01-ai/yi-large", "01.AI Yi Large", "OpenRouter", "01.AI Yi Large via OpenRouter.", ApiProvider.OpenRouter),

        // ── Newest 2026 frontier additions via OpenRouter ────────────
        AiModel("openrouter/auto", "Auto Router", "OpenRouter", "Auto-selects the best model for your prompt.", ApiProvider.OpenRouter),
        AiModel("openrouter/free", "Free Auto Router", "OpenRouter", "Auto-selects from free OpenRouter models.", ApiProvider.OpenRouter),

        // OpenAI 5.x family via OpenRouter
        AiModel("openai/gpt-5.5", "GPT-5.5", "OpenRouter", "OpenAI GPT-5.5 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-5.4", "GPT-5.4", "OpenRouter", "OpenAI GPT-5.4 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-5.4-mini", "GPT-5.4 Mini", "OpenRouter", "OpenAI GPT-5.4 Mini via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-5.2", "GPT-5.2", "OpenRouter", "OpenAI GPT-5.2 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-5.1", "GPT-5.1", "OpenRouter", "OpenAI GPT-5.1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-oss-120b", "GPT-OSS 120B", "OpenRouter", "OpenAI open-weight 120B MoE via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("openai/gpt-oss-20b", "GPT-OSS 20B", "OpenRouter", "OpenAI open-weight 20B via OpenRouter.", ApiProvider.OpenRouter),

        // Anthropic 4.6 / 4.7 family via OpenRouter
        AiModel("anthropic/claude-opus-4.7", "Claude Opus 4.7", "OpenRouter", "Anthropic Opus 4.7 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-opus-4.6", "Claude Opus 4.6", "OpenRouter", "Anthropic Opus 4.6 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-opus-4.6-fast", "Claude Opus 4.6 (Fast)", "OpenRouter", "Fast-mode Opus 4.6 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("anthropic/claude-sonnet-4.6", "Claude Sonnet 4.6", "OpenRouter", "Anthropic Sonnet 4.6 via OpenRouter.", ApiProvider.OpenRouter),

        // Google Gemini 3.x via OpenRouter
        AiModel("google/gemini-3-pro-preview", "Gemini 3 Pro (Preview)", "OpenRouter", "Google Gemini 3 Pro via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-3-flash-preview", "Gemini 3 Flash (Preview)", "OpenRouter", "Google Gemini 3 Flash via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-3.1-flash-lite-preview", "Gemini 3.1 Flash Lite", "OpenRouter", "Lightweight Gemini 3.1 Flash via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-embedding-2-preview", "Gemini Embedding 2 (Preview)", "OpenRouter", "Google multimodal embeddings via OpenRouter.", ApiProvider.OpenRouter),

        // DeepSeek V3.x / V4 via OpenRouter
        AiModel("deepseek/deepseek-v4-pro", "DeepSeek V4 Pro", "OpenRouter", "DeepSeek V4 Pro via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-v4-flash", "DeepSeek V4 Flash", "OpenRouter", "DeepSeek V4 Flash via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-v3.2", "DeepSeek V3.2", "OpenRouter", "DeepSeek V3.2 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-v3", "DeepSeek V3", "OpenRouter", "DeepSeek V3 via OpenRouter.", ApiProvider.OpenRouter),

        // Qwen3 family via OpenRouter
        AiModel("qwen/qwen3-max-thinking", "Qwen3 Max Thinking", "OpenRouter", "Qwen3 flagship reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3-max", "Qwen3 Max", "OpenRouter", "Qwen3 max via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3-235b-a22b-2507", "Qwen3 235B (2507)", "OpenRouter", "Qwen3 235B July build via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3-235b-a22b-thinking-2507", "Qwen3 235B Thinking", "OpenRouter", "Qwen3 235B reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3-32b", "Qwen3 32B", "OpenRouter", "Qwen3 32B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3-30b-a3b", "Qwen3 30B A3B", "OpenRouter", "Qwen3 sparse 30B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3-coder-480b-a35b", "Qwen3 Coder 480B", "OpenRouter", "Qwen3 coder MoE via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3-coder-next", "Qwen3 Coder Next", "OpenRouter", "Sparse Qwen3 coder via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3-vl-plus", "Qwen3 VL Plus", "OpenRouter", "Qwen3 vision via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen3.6-plus", "Qwen3.6 Plus", "OpenRouter", "Qwen3.6 Plus via OpenRouter.", ApiProvider.OpenRouter),

        // Z.ai GLM via OpenRouter
        AiModel("z-ai/glm-4.6", "Z.ai GLM 4.6", "OpenRouter", "Z.ai GLM 4.6 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("z-ai/glm-4.6v", "Z.ai GLM 4.6V", "OpenRouter", "Z.ai GLM 4.6 vision via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("z-ai/glm-4.5", "Z.ai GLM 4.5", "OpenRouter", "Z.ai GLM 4.5 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("z-ai/glm-4-32b", "Z.ai GLM 4 32B", "OpenRouter", "Z.ai GLM 4 32B via OpenRouter.", ApiProvider.OpenRouter),

        // MiniMax via OpenRouter
        AiModel("minimax/minimax-m2.1", "MiniMax M2.1", "OpenRouter", "MiniMax M2.1 coding/agent via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("minimax/minimax-m2", "MiniMax M2", "OpenRouter", "MiniMax M2 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("minimax/minimax-m1", "MiniMax M1", "OpenRouter", "MiniMax M1 via OpenRouter.", ApiProvider.OpenRouter),

        // Moonshot Kimi via OpenRouter
        AiModel("moonshotai/kimi-k2.6", "Kimi K2.6", "OpenRouter", "Moonshot Kimi K2.6 multimodal via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("moonshotai/kimi-k2-thinking", "Kimi K2 Thinking", "OpenRouter", "Kimi K2 reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("moonshotai/kimi-vl-a3b-thinking", "Kimi VL A3B Thinking", "OpenRouter", "Kimi vision reasoner via OpenRouter.", ApiProvider.OpenRouter),

        // inclusionAI Ling/Ring via OpenRouter
        AiModel("inclusionai/ling-2.6-1t", "Ling 2.6 1T", "OpenRouter", "inclusionAI Ling 2.6 1T via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("inclusionai/ling-2.6-1t:free", "Ling 2.6 1T (Free)", "OpenRouter", "Free Ling 2.6 1T via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("inclusionai/ling-2.6-flash", "Ling 2.6 Flash", "OpenRouter", "inclusionAI Ling Flash via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("inclusionai/ling-2.6-flash:free", "Ling 2.6 Flash (Free)", "OpenRouter", "Free Ling 2.6 Flash via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("inclusionai/ring-2.6-1t", "Ring 2.6 1T", "OpenRouter", "inclusionAI Ring 1T thinking via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("inclusionai/ring-2.6-1t:free", "Ring 2.6 1T (Free)", "OpenRouter", "Free Ring 2.6 1T via OpenRouter.", ApiProvider.OpenRouter),

        // Inception diffusion LMs via OpenRouter
        AiModel("inception/mercury-2", "Inception Mercury 2", "OpenRouter", "Diffusion LM Mercury 2 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("inception/mercury", "Inception Mercury", "OpenRouter", "Diffusion LM Mercury via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("inception/mercury-coder", "Inception Mercury Coder", "OpenRouter", "Diffusion code LM via OpenRouter.", ApiProvider.OpenRouter),

        // Mistral updates via OpenRouter
        AiModel("mistralai/mistral-large-2512", "Mistral Large 3 2512", "OpenRouter", "Mistral Large 3 (Dec 2025) via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/mistral-medium-3.5", "Mistral Medium 3.5", "OpenRouter", "Mistral Medium 3.5 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/mistral-medium-3.1", "Mistral Medium 3.1", "OpenRouter", "Mistral Medium 3.1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/codestral-2508", "Codestral 25.08", "OpenRouter", "Mistral Codestral August 2025 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/devstral-medium", "Devstral Medium", "OpenRouter", "Mistral Devstral Medium via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/devstral-small", "Devstral Small", "OpenRouter", "Mistral Devstral Small via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/devstral-small:free", "Devstral Small (Free)", "OpenRouter", "Free Devstral Small via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/magistral-medium-2509", "Magistral Medium 2509", "OpenRouter", "Mistral Magistral Sep 2025 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/magistral-small", "Magistral Small", "OpenRouter", "Mistral Magistral Small via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/mistral-saba", "Mistral Saba", "OpenRouter", "Mistral Saba (Arabic-tuned) via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("mistralai/mistral-nemo", "Mistral Nemo 12B", "OpenRouter", "Open Mistral Nemo via OpenRouter.", ApiProvider.OpenRouter),

        // Meta Llama 4 / 3.x extras via OpenRouter
        AiModel("meta-llama/llama-4-behemoth", "Llama 4 Behemoth", "OpenRouter", "Meta Llama 4 Behemoth via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.2-3b-instruct", "Llama 3.2 3B", "OpenRouter", "Llama 3.2 3B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-3.2-1b-instruct", "Llama 3.2 1B", "OpenRouter", "Llama 3.2 1B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("meta-llama/llama-guard-4-12b", "Llama Guard 4 12B", "OpenRouter", "Meta moderation Llama via OpenRouter.", ApiProvider.OpenRouter),

        // NVIDIA Nemotron via OpenRouter
        AiModel("nvidia/nemotron-3-super-120b-a12b", "Nemotron 3 Super 120B", "OpenRouter", "NVIDIA Nemotron 3 Super via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("nvidia/nemotron-3-super-120b-a12b:free", "Nemotron 3 Super (Free)", "OpenRouter", "Free Nemotron 3 Super via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("nvidia/nemotron-3-nano-omni", "Nemotron 3 Nano Omni", "OpenRouter", "NVIDIA multimodal Nemotron via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("nvidia/llama-3.3-nemotron-super-49b-v1.5", "Llama 3.3 Nemotron Super 49B", "OpenRouter", "NVIDIA Nemotron Super 49B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("nvidia/llama-3.1-nemotron-ultra-253b-v1", "Llama 3.1 Nemotron Ultra 253B", "OpenRouter", "NVIDIA Nemotron Ultra via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("nvidia/llama-3.1-nemotron-70b-instruct", "Llama 3.1 Nemotron 70B", "OpenRouter", "NVIDIA Nemotron 70B via OpenRouter.", ApiProvider.OpenRouter),

        // Tencent Hunyuan via OpenRouter
        AiModel("tencent/hunyuan-a13b-instruct", "Hunyuan A13B", "OpenRouter", "Tencent Hunyuan MoE via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("tencent/hunyuan-a13b-instruct:free", "Hunyuan A13B (Free)", "OpenRouter", "Free Tencent Hunyuan via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("tencent/hunyuan-large", "Hunyuan Large", "OpenRouter", "Tencent Hunyuan Large via OpenRouter.", ApiProvider.OpenRouter),

        // Reka via OpenRouter
        AiModel("rekaai/reka-flash-3", "Reka Flash 3", "OpenRouter", "Reka Flash 3 21B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("rekaai/reka-core", "Reka Core", "OpenRouter", "Reka Core flagship via OpenRouter.", ApiProvider.OpenRouter),

        // AI21 Jamba via OpenRouter
        AiModel("ai21/jamba-1.7-large", "AI21 Jamba 1.7 Large", "OpenRouter", "AI21 Jamba 1.7 Large via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("ai21/jamba-1.7-mini", "AI21 Jamba 1.7 Mini", "OpenRouter", "AI21 Jamba 1.7 Mini via OpenRouter.", ApiProvider.OpenRouter),

        // Sao10K roleplay tunes via OpenRouter
        AiModel("sao10k/l3.3-euryale-70b", "Llama 3.3 Euryale 70B", "OpenRouter", "Sao10K Euryale 70B via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("sao10k/l3.1-euryale-70b", "Llama 3.1 Euryale 70B v2", "OpenRouter", "Sao10K Euryale 70B v2 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("sao10k/l3.1-70b-hanami-x1", "Llama 3.1 70B Hanami x1", "OpenRouter", "Sao10K Hanami x1 via OpenRouter.", ApiProvider.OpenRouter),

        // Cohere updates via OpenRouter
        AiModel("cohere/command-a", "Cohere Command A", "OpenRouter", "Cohere Command A flagship via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("cohere/command-r-08-2024", "Cohere Command R (08-2024)", "OpenRouter", "Cohere Command R Aug 2024 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("cohere/command-r-plus-08-2024", "Cohere Command R+ (08-2024)", "OpenRouter", "Cohere Command R+ Aug 2024 via OpenRouter.", ApiProvider.OpenRouter),

        // Perplexity reasoning/sonar via OpenRouter
        AiModel("perplexity/sonar-reasoning-pro", "Perplexity Sonar Reasoning Pro", "OpenRouter", "Perplexity reasoning Pro via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("perplexity/sonar-reasoning", "Perplexity Sonar Reasoning", "OpenRouter", "Perplexity reasoning via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("perplexity/sonar-deep-research", "Perplexity Sonar Deep Research", "OpenRouter", "Perplexity deep research via OpenRouter.", ApiProvider.OpenRouter),

        // xAI Grok 4.x via OpenRouter
        AiModel("x-ai/grok-4.1", "Grok 4.1", "OpenRouter", "xAI Grok 4.1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("x-ai/grok-4.1-fast", "Grok 4.1 Fast", "OpenRouter", "xAI Grok 4.1 Fast via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("x-ai/grok-4-heavy", "Grok 4 Heavy", "OpenRouter", "xAI Grok 4 Heavy via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("x-ai/grok-2-vision-1212", "Grok 2 Vision", "OpenRouter", "xAI Grok 2 vision via OpenRouter.", ApiProvider.OpenRouter),

        // Free tier OpenRouter models
        AiModel("meta-llama/llama-3.3-70b-instruct:free", "Llama 3.3 70B (Free)", "OpenRouter", "Free Llama 3.3 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-r1:free", "DeepSeek R1 (Free)", "OpenRouter", "Free R1 via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("deepseek/deepseek-chat:free", "DeepSeek V3 (Free)", "OpenRouter", "Free DeepSeek chat via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("google/gemini-2.0-flash-exp:free", "Gemini 2.0 Flash (Free)", "OpenRouter", "Free experimental Gemini via OpenRouter.", ApiProvider.OpenRouter),
        AiModel("qwen/qwen-2.5-72b-instruct:free", "Qwen 2.5 72B (Free)", "OpenRouter", "Free Qwen 2.5 via OpenRouter.", ApiProvider.OpenRouter),

        // ──────────────────────────────────────────────────────────────
        //  OpenAI (direct)
        // ──────────────────────────────────────────────────────────────
        AiModel("gpt-5", "GPT-5", "OpenAI", "OpenAI flagship unified model.", ApiProvider.OpenAI),
        AiModel("gpt-5-mini", "GPT-5 Mini", "OpenAI", "Compact GPT-5.", ApiProvider.OpenAI),
        AiModel("gpt-5-nano", "GPT-5 Nano", "OpenAI", "Smallest GPT-5.", ApiProvider.OpenAI),
        AiModel("gpt-4.1", "GPT-4.1", "OpenAI", "1M-context GPT-4.1.", ApiProvider.OpenAI),
        AiModel("gpt-4.1-mini", "GPT-4.1 Mini", "OpenAI", "Affordable GPT-4.1.", ApiProvider.OpenAI),
        AiModel("gpt-4.1-nano", "GPT-4.1 Nano", "OpenAI", "Tiny GPT-4.1.", ApiProvider.OpenAI),
        AiModel("gpt-4o", "GPT-4o", "OpenAI", "GPT-4 omni multimodal model.", ApiProvider.OpenAI),
        AiModel("gpt-4o-mini", "GPT-4o Mini", "OpenAI", "Fast affordable GPT-4o.", ApiProvider.OpenAI),
        AiModel("chatgpt-4o-latest", "ChatGPT-4o (Latest)", "OpenAI", "Latest ChatGPT-4o snapshot.", ApiProvider.OpenAI),
        AiModel("gpt-4-turbo", "GPT-4 Turbo", "OpenAI", "Legacy GPT-4 Turbo.", ApiProvider.OpenAI),
        AiModel("gpt-4", "GPT-4", "OpenAI", "Original GPT-4.", ApiProvider.OpenAI),
        AiModel("gpt-3.5-turbo", "GPT-3.5 Turbo", "OpenAI", "Legacy fast OpenAI model.", ApiProvider.OpenAI),
        AiModel("o3", "o3", "OpenAI", "Frontier o3 reasoning.", ApiProvider.OpenAI),
        AiModel("o3-mini", "o3 Mini", "OpenAI", "Cost-effective o3 reasoning.", ApiProvider.OpenAI),
        AiModel("o3-pro", "o3 Pro", "OpenAI", "High-effort o3 reasoning.", ApiProvider.OpenAI),
        AiModel("o4-mini", "o4 Mini", "OpenAI", "Newer compact reasoner.", ApiProvider.OpenAI),
        AiModel("o1", "o1", "OpenAI", "Original o1 reasoning model.", ApiProvider.OpenAI),
        AiModel("o1-mini", "o1 Mini", "OpenAI", "Compact o1 reasoning model.", ApiProvider.OpenAI),
        AiModel("o1-pro", "o1 Pro", "OpenAI", "Highest-effort o1 reasoning.", ApiProvider.OpenAI),

        // ──────────────────────────────────────────────────────────────
        //  Anthropic Claude (direct)
        // ──────────────────────────────────────────────────────────────
        AiModel("claude-opus-4-1", "Claude Opus 4.1", "Anthropic", "Top-tier Claude Opus.", ApiProvider.Anthropic),
        AiModel("claude-opus-4-0", "Claude Opus 4", "Anthropic", "Claude Opus 4 baseline.", ApiProvider.Anthropic),
        AiModel("claude-sonnet-4-5", "Claude Sonnet 4.5", "Anthropic", "Latest Claude Sonnet.", ApiProvider.Anthropic),
        AiModel("claude-sonnet-4-0", "Claude Sonnet 4", "Anthropic", "Claude Sonnet 4 baseline.", ApiProvider.Anthropic),
        AiModel("claude-haiku-4-5", "Claude Haiku 4.5", "Anthropic", "Fastest Claude 4-class model.", ApiProvider.Anthropic),
        AiModel("claude-3-7-sonnet-latest", "Claude 3.7 Sonnet", "Anthropic", "Hybrid reasoning Claude 3.7.", ApiProvider.Anthropic),
        AiModel("claude-3-5-sonnet-latest", "Claude 3.5 Sonnet", "Anthropic", "Strong Claude for coding and analysis.", ApiProvider.Anthropic),
        AiModel("claude-3-5-haiku-latest", "Claude 3.5 Haiku", "Anthropic", "Fast Claude 3.5 model.", ApiProvider.Anthropic),
        AiModel("claude-3-opus-latest", "Claude 3 Opus", "Anthropic", "Legacy flagship Claude 3.", ApiProvider.Anthropic),
        AiModel("claude-3-sonnet-20240229", "Claude 3 Sonnet", "Anthropic", "Legacy balanced Claude 3.", ApiProvider.Anthropic),
        AiModel("claude-3-haiku-20240307", "Claude 3 Haiku", "Anthropic", "Legacy compact Claude 3.", ApiProvider.Anthropic),

        // ──────────────────────────────────────────────────────────────
        //  Google Gemini (direct)
        // ──────────────────────────────────────────────────────────────
        AiModel("gemini-2.5-pro", "Gemini 2.5 Pro", "Google", "Top-tier Gemini reasoning model.", ApiProvider.Gemini),
        AiModel("gemini-2.5-flash", "Gemini 2.5 Flash", "Google", "Fast Gemini 2.5 with thinking.", ApiProvider.Gemini),
        AiModel("gemini-2.5-flash-lite", "Gemini 2.5 Flash Lite", "Google", "Most cost-efficient Gemini 2.5.", ApiProvider.Gemini),
        AiModel("gemini-2.0-flash", "Gemini 2.0 Flash", "Google", "Stable Gemini 2.0 Flash.", ApiProvider.Gemini),
        AiModel("gemini-2.0-flash-lite", "Gemini 2.0 Flash Lite", "Google", "Lightweight Gemini 2.0.", ApiProvider.Gemini),
        AiModel("gemini-1.5-pro", "Gemini 1.5 Pro", "Google", "Long-context Gemini 1.5 Pro.", ApiProvider.Gemini),
        AiModel("gemini-1.5-flash", "Gemini 1.5 Flash", "Google", "Fast Gemini 1.5 Flash.", ApiProvider.Gemini),
        AiModel("gemini-1.5-flash-8b", "Gemini 1.5 Flash 8B", "Google", "Smallest Gemini 1.5 variant.", ApiProvider.Gemini),

        // ──────────────────────────────────────────────────────────────
        //  Groq (direct, OpenAI-compatible)
        // ──────────────────────────────────────────────────────────────
        AiModel("llama-3.3-70b-versatile", "Llama 3.3 70B", "Groq", "Versatile Llama 3.3 on Groq.", ApiProvider.Groq),
        AiModel("llama-3.1-8b-instant", "Llama 3.1 8B Instant", "Groq", "Fastest small Llama on Groq.", ApiProvider.Groq),
        AiModel("llama3-70b-8192", "Llama 3 70B", "Groq", "Llama 3 70B on Groq.", ApiProvider.Groq),
        AiModel("llama3-8b-8192", "Llama 3 8B", "Groq", "Llama 3 8B on Groq.", ApiProvider.Groq),
        AiModel("llama-3.2-90b-vision-preview", "Llama 3.2 90B Vision", "Groq", "Llama 3.2 vision on Groq.", ApiProvider.Groq),
        AiModel("llama-3.2-11b-vision-preview", "Llama 3.2 11B Vision", "Groq", "Compact vision Llama on Groq.", ApiProvider.Groq),
        AiModel("llama-3.2-3b-preview", "Llama 3.2 3B", "Groq", "Tiny Llama 3.2 on Groq.", ApiProvider.Groq),
        AiModel("llama-3.2-1b-preview", "Llama 3.2 1B", "Groq", "Smallest Llama 3.2 on Groq.", ApiProvider.Groq),
        AiModel("mixtral-8x7b-32768", "Mixtral 8x7B", "Groq", "Mixture-of-experts Mixtral on Groq.", ApiProvider.Groq),
        AiModel("gemma2-9b-it", "Gemma 2 9B", "Groq", "Google Gemma 2 on Groq.", ApiProvider.Groq),
        AiModel("qwen-2.5-32b", "Qwen 2.5 32B", "Groq", "Qwen 2.5 on Groq.", ApiProvider.Groq),
        AiModel("qwen-qwq-32b", "Qwen QwQ 32B", "Groq", "Qwen reasoning on Groq.", ApiProvider.Groq),
        AiModel("deepseek-r1-distill-llama-70b", "DeepSeek R1 Distill 70B", "Groq", "Distilled R1 on Groq.", ApiProvider.Groq),
        AiModel("deepseek-r1-distill-qwen-32b", "DeepSeek R1 Distill Qwen 32B", "Groq", "R1-distilled Qwen on Groq.", ApiProvider.Groq),
        AiModel("allam-2-7b", "ALLaM 2 7B", "Groq", "Arabic-focused ALLaM on Groq.", ApiProvider.Groq),

        // ──────────────────────────────────────────────────────────────
        //  Mistral (direct)
        // ──────────────────────────────────────────────────────────────
        AiModel("mistral-large-latest", "Mistral Large", "Mistral", "Mistral flagship reasoning model.", ApiProvider.Mistral),
        AiModel("mistral-medium-latest", "Mistral Medium", "Mistral", "Balanced Mistral model.", ApiProvider.Mistral),
        AiModel("mistral-small-latest", "Mistral Small", "Mistral", "Affordable Mistral model.", ApiProvider.Mistral),
        AiModel("ministral-8b-latest", "Ministral 8B", "Mistral", "Edge-tier Mistral 8B.", ApiProvider.Mistral),
        AiModel("ministral-3b-latest", "Ministral 3B", "Mistral", "Tiny Ministral 3B.", ApiProvider.Mistral),
        AiModel("open-mistral-nemo", "Open Mistral Nemo", "Mistral", "Open Mistral Nemo 12B.", ApiProvider.Mistral),
        AiModel("codestral-latest", "Codestral", "Mistral", "Mistral coding model.", ApiProvider.Mistral),
        AiModel("devstral-medium-latest", "Devstral Medium", "Mistral", "Agentic dev Mistral.", ApiProvider.Mistral),
        AiModel("devstral-small-latest", "Devstral Small", "Mistral", "Compact agentic dev Mistral.", ApiProvider.Mistral),
        AiModel("magistral-medium-latest", "Magistral Medium", "Mistral", "Mistral reasoning model.", ApiProvider.Mistral),
        AiModel("magistral-small-latest", "Magistral Small", "Mistral", "Compact Magistral reasoner.", ApiProvider.Mistral),
        AiModel("pixtral-large-latest", "Pixtral Large", "Mistral", "Mistral vision flagship.", ApiProvider.Mistral),
        AiModel("pixtral-12b-2409", "Pixtral 12B", "Mistral", "Compact Mistral vision model.", ApiProvider.Mistral),
        AiModel("mistral-saba-latest", "Mistral Saba", "Mistral", "Arabic/Middle East tuned Mistral.", ApiProvider.Mistral),

        // ──────────────────────────────────────────────────────────────
        //  DeepSeek (direct)
        // ──────────────────────────────────────────────────────────────
        AiModel("deepseek-chat", "DeepSeek Chat", "DeepSeek", "DeepSeek non-thinking chat model.", ApiProvider.DeepSeek),
        AiModel("deepseek-reasoner", "DeepSeek Reasoner", "DeepSeek", "DeepSeek thinking reasoning model.", ApiProvider.DeepSeek),

        // ──────────────────────────────────────────────────────────────
        //  xAI Grok (direct)
        // ──────────────────────────────────────────────────────────────
        AiModel("grok-4-0709", "Grok 4", "xAI", "xAI Grok 4 flagship.", ApiProvider.XAI),
        AiModel("grok-4-fast-reasoning", "Grok 4 Fast (Reasoning)", "xAI", "Cheap Grok 4 reasoning.", ApiProvider.XAI),
        AiModel("grok-4-fast-non-reasoning", "Grok 4 Fast", "xAI", "Cheap Grok 4 non-reasoning.", ApiProvider.XAI),
        AiModel("grok-code-fast-1", "Grok Code Fast 1", "xAI", "Agentic coding Grok.", ApiProvider.XAI),
        AiModel("grok-3", "Grok 3", "xAI", "xAI Grok 3 enterprise model.", ApiProvider.XAI),
        AiModel("grok-3-fast", "Grok 3 Fast", "xAI", "Faster Grok 3 variant.", ApiProvider.XAI),
        AiModel("grok-3-mini", "Grok 3 Mini", "xAI", "Compact Grok 3.", ApiProvider.XAI),
        AiModel("grok-3-mini-fast", "Grok 3 Mini Fast", "xAI", "Fastest small Grok.", ApiProvider.XAI),
        AiModel("grok-2-latest", "Grok 2", "xAI", "Legacy Grok 2.", ApiProvider.XAI),
        AiModel("grok-2-vision-1212", "Grok 2 Vision", "xAI", "Grok 2 with vision.", ApiProvider.XAI),
        AiModel("grok-beta", "Grok Beta", "xAI", "Beta Grok endpoint.", ApiProvider.XAI),

        // ──────────────────────────────────────────────────────────────
        //  Cohere (direct, OpenAI-compatible at api.cohere.com/compatibility/v1)
        // ──────────────────────────────────────────────────────────────
        AiModel("command-a-03-2025", "Command A", "Cohere", "Cohere Command A flagship.", ApiProvider.Cohere),
        AiModel("command-r-plus-08-2024", "Command R+ (08-2024)", "Cohere", "Cohere Command R+ Aug 2024.", ApiProvider.Cohere),
        AiModel("command-r-plus", "Command R+", "Cohere", "Cohere Command R+ flagship.", ApiProvider.Cohere),
        AiModel("command-r-08-2024", "Command R (08-2024)", "Cohere", "Cohere Command R Aug 2024.", ApiProvider.Cohere),
        AiModel("command-r", "Command R", "Cohere", "Cohere Command R baseline.", ApiProvider.Cohere),
        AiModel("command-r7b-12-2024", "Command R7B", "Cohere", "Compact Cohere Command R7B.", ApiProvider.Cohere),
        AiModel("command", "Command", "Cohere", "Legacy Cohere Command model.", ApiProvider.Cohere),
        AiModel("command-light", "Command Light", "Cohere", "Lightweight Cohere Command.", ApiProvider.Cohere),

        // ──────────────────────────────────────────────────────────────
        //  Perplexity (direct, api.perplexity.ai/chat/completions)
        // ──────────────────────────────────────────────────────────────
        AiModel("sonar-pro", "Sonar Pro", "Perplexity", "Perplexity Sonar Pro web-grounded model.", ApiProvider.Perplexity),
        AiModel("sonar", "Sonar", "Perplexity", "Perplexity Sonar fast web-grounded model.", ApiProvider.Perplexity),
        AiModel("sonar-reasoning-pro", "Sonar Reasoning Pro", "Perplexity", "Perplexity reasoning Pro with web search.", ApiProvider.Perplexity),
        AiModel("sonar-reasoning", "Sonar Reasoning", "Perplexity", "Perplexity reasoning with web search.", ApiProvider.Perplexity),
        AiModel("sonar-deep-research", "Sonar Deep Research", "Perplexity", "Perplexity multi-step deep research.", ApiProvider.Perplexity),

        // ──────────────────────────────────────────────────────────────
        //  Together AI (direct, api.together.xyz/v1/chat/completions)
        // ──────────────────────────────────────────────────────────────
        AiModel("meta-llama/Llama-3.3-70B-Instruct-Turbo", "Llama 3.3 70B Turbo", "Together", "Llama 3.3 70B on Together.", ApiProvider.Together),
        AiModel("meta-llama/Meta-Llama-3.1-405B-Instruct-Turbo", "Llama 3.1 405B Turbo", "Together", "Llama 3.1 405B on Together.", ApiProvider.Together),
        AiModel("meta-llama/Meta-Llama-3.1-70B-Instruct-Turbo", "Llama 3.1 70B Turbo", "Together", "Llama 3.1 70B on Together.", ApiProvider.Together),
        AiModel("meta-llama/Meta-Llama-3.1-8B-Instruct-Turbo", "Llama 3.1 8B Turbo", "Together", "Llama 3.1 8B on Together.", ApiProvider.Together),
        AiModel("meta-llama/Llama-4-Maverick-17B-128E-Instruct-FP8", "Llama 4 Maverick", "Together", "Llama 4 Maverick on Together.", ApiProvider.Together),
        AiModel("meta-llama/Llama-4-Scout-17B-16E-Instruct", "Llama 4 Scout", "Together", "Llama 4 Scout on Together.", ApiProvider.Together),
        AiModel("deepseek-ai/DeepSeek-V3", "DeepSeek V3", "Together", "DeepSeek V3 on Together.", ApiProvider.Together),
        AiModel("deepseek-ai/DeepSeek-R1", "DeepSeek R1", "Together", "DeepSeek R1 on Together.", ApiProvider.Together),
        AiModel("Qwen/Qwen2.5-72B-Instruct-Turbo", "Qwen 2.5 72B Turbo", "Together", "Qwen 2.5 72B on Together.", ApiProvider.Together),
        AiModel("Qwen/Qwen2.5-Coder-32B-Instruct", "Qwen 2.5 Coder 32B", "Together", "Qwen 2.5 Coder on Together.", ApiProvider.Together),
        AiModel("Qwen/QwQ-32B", "Qwen QwQ 32B", "Together", "Qwen reasoning on Together.", ApiProvider.Together),
        AiModel("mistralai/Mixtral-8x7B-Instruct-v0.1", "Mixtral 8x7B", "Together", "Mixtral 8x7B on Together.", ApiProvider.Together),
        AiModel("mistralai/Mixtral-8x22B-Instruct-v0.1", "Mixtral 8x22B", "Together", "Mixtral 8x22B on Together.", ApiProvider.Together),
        AiModel("google/gemma-2-27b-it", "Gemma 2 27B", "Together", "Google Gemma 2 27B on Together.", ApiProvider.Together),
        AiModel("nvidia/Llama-3.1-Nemotron-70B-Instruct-HF", "Llama 3.1 Nemotron 70B", "Together", "NVIDIA Nemotron on Together.", ApiProvider.Together),

        // ──────────────────────────────────────────────────────────────
        //  Fireworks AI (direct, api.fireworks.ai/inference/v1/chat/completions)
        // ──────────────────────────────────────────────────────────────
        AiModel("accounts/fireworks/models/llama-v3p3-70b-instruct", "Llama 3.3 70B", "Fireworks", "Llama 3.3 70B on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/llama-v3p1-405b-instruct", "Llama 3.1 405B", "Fireworks", "Llama 3.1 405B on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/llama-v3p1-70b-instruct", "Llama 3.1 70B", "Fireworks", "Llama 3.1 70B on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/llama-v3p1-8b-instruct", "Llama 3.1 8B", "Fireworks", "Llama 3.1 8B on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/llama4-maverick-instruct-basic", "Llama 4 Maverick", "Fireworks", "Llama 4 Maverick on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/llama4-scout-instruct-basic", "Llama 4 Scout", "Fireworks", "Llama 4 Scout on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/deepseek-v3", "DeepSeek V3", "Fireworks", "DeepSeek V3 on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/deepseek-r1", "DeepSeek R1", "Fireworks", "DeepSeek R1 on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/qwen2p5-72b-instruct", "Qwen 2.5 72B", "Fireworks", "Qwen 2.5 72B on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/qwen2p5-coder-32b-instruct", "Qwen 2.5 Coder 32B", "Fireworks", "Qwen Coder on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/qwq-32b", "Qwen QwQ 32B", "Fireworks", "Qwen reasoning on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/mixtral-8x22b-instruct", "Mixtral 8x22B", "Fireworks", "Mixtral 8x22B on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/mixtral-8x7b-instruct", "Mixtral 8x7B", "Fireworks", "Mixtral 8x7B on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/gpt-oss-120b", "GPT-OSS 120B", "Fireworks", "OpenAI open-weight 120B on Fireworks.", ApiProvider.Fireworks),
        AiModel("accounts/fireworks/models/gpt-oss-20b", "GPT-OSS 20B", "Fireworks", "OpenAI open-weight 20B on Fireworks.", ApiProvider.Fireworks),

        // ──────────────────────────────────────────────────────────────
        //  Moonshot Kimi (direct, api.moonshot.ai/v1/chat/completions)
        // ──────────────────────────────────────────────────────────────
        AiModel("kimi-k2-0905-preview", "Kimi K2 (0905)", "Moonshot", "Kimi K2 0905 preview.", ApiProvider.Moonshot),
        AiModel("kimi-k2-0711-preview", "Kimi K2 (0711)", "Moonshot", "Kimi K2 0711 preview.", ApiProvider.Moonshot),
        AiModel("kimi-latest", "Kimi Latest", "Moonshot", "Latest Kimi default model.", ApiProvider.Moonshot),
        AiModel("moonshot-v1-128k", "Moonshot v1 128k", "Moonshot", "Moonshot v1 128k context.", ApiProvider.Moonshot),
        AiModel("moonshot-v1-32k", "Moonshot v1 32k", "Moonshot", "Moonshot v1 32k context.", ApiProvider.Moonshot),
        AiModel("moonshot-v1-8k", "Moonshot v1 8k", "Moonshot", "Moonshot v1 8k context.", ApiProvider.Moonshot),
        AiModel("moonshot-v1-128k-vision-preview", "Moonshot v1 Vision", "Moonshot", "Moonshot vision preview.", ApiProvider.Moonshot),

        // ──────────────────────────────────────────────────────────────
        //  Z.ai GLM (direct, api.z.ai/api/paas/v4/chat/completions)
        // ──────────────────────────────────────────────────────────────
        AiModel("glm-4.6", "GLM 4.6", "Z.ai", "Z.ai GLM 4.6 flagship.", ApiProvider.ZAI),
        AiModel("glm-4.5", "GLM 4.5", "Z.ai", "Z.ai GLM 4.5.", ApiProvider.ZAI),
        AiModel("glm-4.5-air", "GLM 4.5 Air", "Z.ai", "Lightweight GLM 4.5 Air.", ApiProvider.ZAI),
        AiModel("glm-4.5-x", "GLM 4.5 X", "Z.ai", "GLM 4.5 X variant.", ApiProvider.ZAI),
        AiModel("glm-4.5-flash", "GLM 4.5 Flash", "Z.ai", "Fast GLM 4.5 Flash.", ApiProvider.ZAI),
        AiModel("glm-4-plus", "GLM 4 Plus", "Z.ai", "Z.ai GLM 4 Plus.", ApiProvider.ZAI),
        AiModel("glm-4-air", "GLM 4 Air", "Z.ai", "Z.ai GLM 4 Air.", ApiProvider.ZAI),
        AiModel("glm-4-airx", "GLM 4 AirX", "Z.ai", "Z.ai GLM 4 AirX.", ApiProvider.ZAI),
        AiModel("glm-4-flash", "GLM 4 Flash", "Z.ai", "Fast Z.ai GLM 4 Flash.", ApiProvider.ZAI),
        AiModel("glm-4v-plus", "GLM 4V Plus", "Z.ai", "Z.ai vision flagship.", ApiProvider.ZAI),
        AiModel("glm-4v", "GLM 4V", "Z.ai", "Z.ai vision baseline.", ApiProvider.ZAI),
        AiModel("glm-zero-preview", "GLM Zero (Preview)", "Z.ai", "Z.ai reasoning preview.", ApiProvider.ZAI),

        // ──────────────────────────────────────────────────────────────
        //  NVIDIA NIM (direct, integrate.api.nvidia.com/v1/chat/completions)
        // ──────────────────────────────────────────────────────────────
        AiModel("nvidia/llama-3.1-nemotron-ultra-253b-v1", "Llama 3.1 Nemotron Ultra 253B", "NVIDIA", "NVIDIA Nemotron Ultra reasoner.", ApiProvider.NvidiaNim),
        AiModel("nvidia/llama-3.3-nemotron-super-49b-v1", "Llama 3.3 Nemotron Super 49B", "NVIDIA", "NVIDIA Nemotron Super 49B.", ApiProvider.NvidiaNim),
        AiModel("nvidia/llama-3.1-nemotron-70b-instruct", "Llama 3.1 Nemotron 70B", "NVIDIA", "NVIDIA Nemotron 70B.", ApiProvider.NvidiaNim),
        AiModel("nvidia/llama-3.1-nemotron-nano-8b-v1", "Llama 3.1 Nemotron Nano 8B", "NVIDIA", "NVIDIA Nemotron Nano 8B.", ApiProvider.NvidiaNim),
        AiModel("nvidia/nemotron-nano-9b-v2", "Nemotron Nano 9B v2", "NVIDIA", "NVIDIA Nemotron Nano 9B v2.", ApiProvider.NvidiaNim),
        AiModel("meta/llama-3.3-70b-instruct", "Llama 3.3 70B", "NVIDIA", "Meta Llama 3.3 70B on NIM.", ApiProvider.NvidiaNim),
        AiModel("meta/llama-3.1-405b-instruct", "Llama 3.1 405B", "NVIDIA", "Meta Llama 3.1 405B on NIM.", ApiProvider.NvidiaNim),
        AiModel("meta/llama-3.1-70b-instruct", "Llama 3.1 70B", "NVIDIA", "Meta Llama 3.1 70B on NIM.", ApiProvider.NvidiaNim),
        AiModel("meta/llama-3.1-8b-instruct", "Llama 3.1 8B", "NVIDIA", "Meta Llama 3.1 8B on NIM.", ApiProvider.NvidiaNim),
        AiModel("meta/llama-4-maverick-17b-128e-instruct", "Llama 4 Maverick", "NVIDIA", "Meta Llama 4 Maverick on NIM.", ApiProvider.NvidiaNim),
        AiModel("meta/llama-4-scout-17b-16e-instruct", "Llama 4 Scout", "NVIDIA", "Meta Llama 4 Scout on NIM.", ApiProvider.NvidiaNim),
        AiModel("deepseek-ai/deepseek-v3", "DeepSeek V3", "NVIDIA", "DeepSeek V3 on NIM.", ApiProvider.NvidiaNim),
        AiModel("deepseek-ai/deepseek-r1", "DeepSeek R1", "NVIDIA", "DeepSeek R1 on NIM.", ApiProvider.NvidiaNim),
        AiModel("qwen/qwen2.5-72b-instruct", "Qwen 2.5 72B", "NVIDIA", "Qwen 2.5 72B on NIM.", ApiProvider.NvidiaNim),
        AiModel("qwen/qwen2.5-coder-32b-instruct", "Qwen 2.5 Coder 32B", "NVIDIA", "Qwen 2.5 Coder on NIM.", ApiProvider.NvidiaNim),
        AiModel("mistralai/mixtral-8x22b-instruct-v0.1", "Mixtral 8x22B", "NVIDIA", "Mixtral 8x22B on NIM.", ApiProvider.NvidiaNim),
        AiModel("mistralai/mistral-large-2-instruct", "Mistral Large 2", "NVIDIA", "Mistral Large 2 on NIM.", ApiProvider.NvidiaNim),
        AiModel("microsoft/phi-4", "Microsoft Phi-4", "NVIDIA", "Microsoft Phi-4 on NIM.", ApiProvider.NvidiaNim),
        AiModel("google/gemma-2-27b-it", "Gemma 2 27B", "NVIDIA", "Google Gemma 2 27B on NIM.", ApiProvider.NvidiaNim),

        // ──────────────────────────────────────────────────────────────
        //  Hugging Face Inference Providers (router.huggingface.co/v1/chat/completions)
        // ──────────────────────────────────────────────────────────────
        AiModel("meta-llama/Llama-3.3-70B-Instruct", "Llama 3.3 70B", "HuggingFace", "Llama 3.3 70B on HF Inference.", ApiProvider.HuggingFace),
        AiModel("meta-llama/Llama-3.1-70B-Instruct", "Llama 3.1 70B", "HuggingFace", "Llama 3.1 70B on HF Inference.", ApiProvider.HuggingFace),
        AiModel("meta-llama/Llama-3.1-8B-Instruct", "Llama 3.1 8B", "HuggingFace", "Llama 3.1 8B on HF Inference.", ApiProvider.HuggingFace),
        AiModel("meta-llama/Llama-4-Maverick-17B-128E-Instruct", "Llama 4 Maverick", "HuggingFace", "Llama 4 Maverick on HF Inference.", ApiProvider.HuggingFace),
        AiModel("meta-llama/Llama-4-Scout-17B-16E-Instruct", "Llama 4 Scout", "HuggingFace", "Llama 4 Scout on HF Inference.", ApiProvider.HuggingFace),
        AiModel("deepseek-ai/DeepSeek-V3", "DeepSeek V3", "HuggingFace", "DeepSeek V3 on HF Inference.", ApiProvider.HuggingFace),
        AiModel("deepseek-ai/DeepSeek-R1", "DeepSeek R1", "HuggingFace", "DeepSeek R1 on HF Inference.", ApiProvider.HuggingFace),
        AiModel("Qwen/Qwen2.5-72B-Instruct", "Qwen 2.5 72B", "HuggingFace", "Qwen 2.5 72B on HF Inference.", ApiProvider.HuggingFace),
        AiModel("Qwen/Qwen2.5-Coder-32B-Instruct", "Qwen 2.5 Coder 32B", "HuggingFace", "Qwen 2.5 Coder on HF Inference.", ApiProvider.HuggingFace),
        AiModel("Qwen/QwQ-32B", "Qwen QwQ 32B", "HuggingFace", "Qwen reasoning on HF Inference.", ApiProvider.HuggingFace),
        AiModel("mistralai/Mistral-Nemo-Instruct-2407", "Mistral Nemo", "HuggingFace", "Mistral Nemo on HF Inference.", ApiProvider.HuggingFace),
        AiModel("mistralai/Mixtral-8x7B-Instruct-v0.1", "Mixtral 8x7B", "HuggingFace", "Mixtral 8x7B on HF Inference.", ApiProvider.HuggingFace),
        AiModel("google/gemma-2-27b-it", "Gemma 2 27B", "HuggingFace", "Gemma 2 27B on HF Inference.", ApiProvider.HuggingFace),
        AiModel("microsoft/phi-4", "Microsoft Phi-4", "HuggingFace", "Microsoft Phi-4 on HF Inference.", ApiProvider.HuggingFace),
        AiModel("HuggingFaceH4/zephyr-7b-beta", "Zephyr 7B Beta", "HuggingFace", "Hugging Face Zephyr 7B.", ApiProvider.HuggingFace),
        AiModel("openai/gpt-oss-120b", "GPT-OSS 120B", "HuggingFace", "OpenAI open-weight 120B on HF Inference.", ApiProvider.HuggingFace),
        AiModel("openai/gpt-oss-20b", "GPT-OSS 20B", "HuggingFace", "OpenAI open-weight 20B on HF Inference.", ApiProvider.HuggingFace)
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
