package com.mrrobot.aiworkspace.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "mr_robot_settings")

enum class AppThemeMode {
    Auto,
    Dark,
    Light,
    Cyber,
    Hacker
}

data class AppSettings(
    val apiKey: String = "",
    val model: String = "openai/gpt-4o-mini",
    val themeMode: AppThemeMode = AppThemeMode.Auto,
    val selectedProvider: ApiProvider = ApiProvider.OpenRouter,

    val openRouterApiKey: String = "",
    val openAiApiKey: String = "",
    val anthropicApiKey: String = "",
    val geminiApiKey: String = "",
    val groqApiKey: String = "",
    val mistralApiKey: String = "",
    val deepSeekApiKey: String = "",
    val xAiApiKey: String = "",

    val openRouterModel: String = "openai/gpt-4o-mini",
    val openAiModel: String = "gpt-4o-mini",
    val anthropicModel: String = "claude-3-5-sonnet-latest",
    val geminiModel: String = "gemini-1.5-flash",
    val groqModel: String = "llama-3.3-70b-versatile",
    val mistralModel: String = "mistral-large-latest",
    val deepSeekModel: String = "deepseek-chat",
    val xAiModel: String = "grok-2-latest"
) {
    fun keyFor(provider: ApiProvider): String {
        return when (provider) {
            ApiProvider.OpenRouter -> openRouterApiKey.ifBlank { apiKey }
            ApiProvider.OpenAI -> openAiApiKey
            ApiProvider.Anthropic -> anthropicApiKey
            ApiProvider.Gemini -> geminiApiKey
            ApiProvider.Groq -> groqApiKey
            ApiProvider.Mistral -> mistralApiKey
            ApiProvider.DeepSeek -> deepSeekApiKey
            ApiProvider.XAI -> xAiApiKey
        }
    }

    fun modelFor(provider: ApiProvider): String {
        val stored = when (provider) {
            ApiProvider.OpenRouter -> openRouterModel.ifBlank { model }
            ApiProvider.OpenAI -> openAiModel
            ApiProvider.Anthropic -> anthropicModel
            ApiProvider.Gemini -> geminiModel
            ApiProvider.Groq -> groqModel
            ApiProvider.Mistral -> mistralModel
            ApiProvider.DeepSeek -> deepSeekModel
            ApiProvider.XAI -> xAiModel
        }

        return AiModels.byIdOrNull(stored)
            ?.takeIf { it.apiProvider == provider }
            ?.id
            ?: AiModels.defaultForProvider(provider).id
    }

    fun activeApiKey(): String {
        return keyFor(selectedProvider)
    }

    fun activeModel(): String {
        return modelFor(selectedProvider)
    }

    fun hasActiveConfiguration(): Boolean {
        return activeApiKey().isNotBlank()
    }

    fun configuredProviderModels(): List<ProviderModelConfig> {
        return ApiProvider.values()
            .map { provider ->
                ProviderModelConfig(
                    provider = provider,
                    modelId = modelFor(provider),
                    apiKey = keyFor(provider)
                )
            }
            .filter { it.hasKey }
    }
}

class SettingsStore(private val context: Context) {

    private object Keys {
        val API_KEY = stringPreferencesKey("openrouter_api_key")
        val MODEL = stringPreferencesKey("selected_model")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SELECTED_PROVIDER = stringPreferencesKey("selected_provider")

        val OPENROUTER_API_KEY = stringPreferencesKey("api_key_openrouter")
        val OPENAI_API_KEY = stringPreferencesKey("api_key_openai")
        val ANTHROPIC_API_KEY = stringPreferencesKey("api_key_anthropic")
        val GEMINI_API_KEY = stringPreferencesKey("api_key_gemini")
        val GROQ_API_KEY = stringPreferencesKey("api_key_groq")
        val MISTRAL_API_KEY = stringPreferencesKey("api_key_mistral")
        val DEEPSEEK_API_KEY = stringPreferencesKey("api_key_deepseek")
        val XAI_API_KEY = stringPreferencesKey("api_key_xai")

        val OPENROUTER_MODEL = stringPreferencesKey("model_openrouter")
        val OPENAI_MODEL = stringPreferencesKey("model_openai")
        val ANTHROPIC_MODEL = stringPreferencesKey("model_anthropic")
        val GEMINI_MODEL = stringPreferencesKey("model_gemini")
        val GROQ_MODEL = stringPreferencesKey("model_groq")
        val MISTRAL_MODEL = stringPreferencesKey("model_mistral")
        val DEEPSEEK_MODEL = stringPreferencesKey("model_deepseek")
        val XAI_MODEL = stringPreferencesKey("model_xai")
    }

    val settingsFlow: Flow<AppSettings> =
        context.settingsDataStore.data.map { prefs ->
            val legacyOpenRouterKey = prefs[Keys.API_KEY] ?: ""
            val openRouterKey = prefs[Keys.OPENROUTER_API_KEY] ?: legacyOpenRouterKey

            val selectedProvider = runCatching {
                ApiProvider.valueOf(prefs[Keys.SELECTED_PROVIDER] ?: ApiProvider.OpenRouter.name)
            }.getOrDefault(ApiProvider.OpenRouter)

            val openRouterModel = normalizeModel(ApiProvider.OpenRouter, prefs[Keys.OPENROUTER_MODEL] ?: prefs[Keys.MODEL])
            val openAiModel = normalizeModel(ApiProvider.OpenAI, prefs[Keys.OPENAI_MODEL])
            val anthropicModel = normalizeModel(ApiProvider.Anthropic, prefs[Keys.ANTHROPIC_MODEL])
            val geminiModel = normalizeModel(ApiProvider.Gemini, prefs[Keys.GEMINI_MODEL])
            val groqModel = normalizeModel(ApiProvider.Groq, prefs[Keys.GROQ_MODEL])
            val mistralModel = normalizeModel(ApiProvider.Mistral, prefs[Keys.MISTRAL_MODEL])
            val deepSeekModel = normalizeModel(ApiProvider.DeepSeek, prefs[Keys.DEEPSEEK_MODEL])
            val xAiModel = normalizeModel(ApiProvider.XAI, prefs[Keys.XAI_MODEL])

            val activeModel = when (selectedProvider) {
                ApiProvider.OpenRouter -> openRouterModel
                ApiProvider.OpenAI -> openAiModel
                ApiProvider.Anthropic -> anthropicModel
                ApiProvider.Gemini -> geminiModel
                ApiProvider.Groq -> groqModel
                ApiProvider.Mistral -> mistralModel
                ApiProvider.DeepSeek -> deepSeekModel
                ApiProvider.XAI -> xAiModel
            }

            AppSettings(
                apiKey = openRouterKey,
                model = activeModel,
                themeMode = runCatching {
                    AppThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: AppThemeMode.Auto.name)
                }.getOrDefault(AppThemeMode.Auto),
                selectedProvider = selectedProvider,

                openRouterApiKey = openRouterKey,
                openAiApiKey = prefs[Keys.OPENAI_API_KEY] ?: "",
                anthropicApiKey = prefs[Keys.ANTHROPIC_API_KEY] ?: "",
                geminiApiKey = prefs[Keys.GEMINI_API_KEY] ?: "",
                groqApiKey = prefs[Keys.GROQ_API_KEY] ?: "",
                mistralApiKey = prefs[Keys.MISTRAL_API_KEY] ?: "",
                deepSeekApiKey = prefs[Keys.DEEPSEEK_API_KEY] ?: "",
                xAiApiKey = prefs[Keys.XAI_API_KEY] ?: "",

                openRouterModel = openRouterModel,
                openAiModel = openAiModel,
                anthropicModel = anthropicModel,
                geminiModel = geminiModel,
                groqModel = groqModel,
                mistralModel = mistralModel,
                deepSeekModel = deepSeekModel,
                xAiModel = xAiModel
            )
        }

    suspend fun saveSettings(
        apiKey: String,
        model: String,
        themeMode: AppThemeMode
    ) {
        saveAllSettings(
            selectedProvider = ApiProvider.OpenRouter,
            themeMode = themeMode,
            openRouterApiKey = apiKey,
            openAiApiKey = "",
            anthropicApiKey = "",
            geminiApiKey = "",
            groqApiKey = "",
            mistralApiKey = "",
            deepSeekApiKey = "",
            xAiApiKey = "",
            openRouterModel = model,
            openAiModel = AiModels.defaultForProvider(ApiProvider.OpenAI).id,
            anthropicModel = AiModels.defaultForProvider(ApiProvider.Anthropic).id,
            geminiModel = AiModels.defaultForProvider(ApiProvider.Gemini).id,
            groqModel = AiModels.defaultForProvider(ApiProvider.Groq).id,
            mistralModel = AiModels.defaultForProvider(ApiProvider.Mistral).id,
            deepSeekModel = AiModels.defaultForProvider(ApiProvider.DeepSeek).id,
            xAiModel = AiModels.defaultForProvider(ApiProvider.XAI).id
        )
    }

    suspend fun saveAllSettings(
        selectedProvider: ApiProvider,
        themeMode: AppThemeMode,

        openRouterApiKey: String,
        openAiApiKey: String,
        anthropicApiKey: String,
        geminiApiKey: String,
        groqApiKey: String,
        mistralApiKey: String,
        deepSeekApiKey: String,
        xAiApiKey: String,

        openRouterModel: String,
        openAiModel: String,
        anthropicModel: String,
        geminiModel: String,
        groqModel: String,
        mistralModel: String,
        deepSeekModel: String,
        xAiModel: String
    ) {
        val safeOpenRouterModel = normalizeModel(ApiProvider.OpenRouter, openRouterModel)
        val safeOpenAiModel = normalizeModel(ApiProvider.OpenAI, openAiModel)
        val safeAnthropicModel = normalizeModel(ApiProvider.Anthropic, anthropicModel)
        val safeGeminiModel = normalizeModel(ApiProvider.Gemini, geminiModel)
        val safeGroqModel = normalizeModel(ApiProvider.Groq, groqModel)
        val safeMistralModel = normalizeModel(ApiProvider.Mistral, mistralModel)
        val safeDeepSeekModel = normalizeModel(ApiProvider.DeepSeek, deepSeekModel)
        val safeXAiModel = normalizeModel(ApiProvider.XAI, xAiModel)

        val activeModel = when (selectedProvider) {
            ApiProvider.OpenRouter -> safeOpenRouterModel
            ApiProvider.OpenAI -> safeOpenAiModel
            ApiProvider.Anthropic -> safeAnthropicModel
            ApiProvider.Gemini -> safeGeminiModel
            ApiProvider.Groq -> safeGroqModel
            ApiProvider.Mistral -> safeMistralModel
            ApiProvider.DeepSeek -> safeDeepSeekModel
            ApiProvider.XAI -> safeXAiModel
        }

        context.settingsDataStore.edit { prefs ->
            prefs[Keys.SELECTED_PROVIDER] = selectedProvider.name
            prefs[Keys.MODEL] = activeModel
            prefs[Keys.THEME_MODE] = themeMode.name

            prefs[Keys.OPENROUTER_API_KEY] = openRouterApiKey.trim()
            prefs[Keys.API_KEY] = openRouterApiKey.trim()
            prefs[Keys.OPENAI_API_KEY] = openAiApiKey.trim()
            prefs[Keys.ANTHROPIC_API_KEY] = anthropicApiKey.trim()
            prefs[Keys.GEMINI_API_KEY] = geminiApiKey.trim()
            prefs[Keys.GROQ_API_KEY] = groqApiKey.trim()
            prefs[Keys.MISTRAL_API_KEY] = mistralApiKey.trim()
            prefs[Keys.DEEPSEEK_API_KEY] = deepSeekApiKey.trim()
            prefs[Keys.XAI_API_KEY] = xAiApiKey.trim()

            prefs[Keys.OPENROUTER_MODEL] = safeOpenRouterModel
            prefs[Keys.OPENAI_MODEL] = safeOpenAiModel
            prefs[Keys.ANTHROPIC_MODEL] = safeAnthropicModel
            prefs[Keys.GEMINI_MODEL] = safeGeminiModel
            prefs[Keys.GROQ_MODEL] = safeGroqModel
            prefs[Keys.MISTRAL_MODEL] = safeMistralModel
            prefs[Keys.DEEPSEEK_MODEL] = safeDeepSeekModel
            prefs[Keys.XAI_MODEL] = safeXAiModel
        }
    }

    suspend fun clearSettings() {
        context.settingsDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private fun normalizeModel(
        provider: ApiProvider,
        value: String?
    ): String {
        return AiModels.byIdOrNull(value.orEmpty())
            ?.takeIf { it.apiProvider == provider }
            ?.id
            ?: AiModels.defaultForProvider(provider).id
    }
}
