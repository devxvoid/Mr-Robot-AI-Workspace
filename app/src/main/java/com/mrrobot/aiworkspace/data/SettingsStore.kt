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
    Cyberpunk,
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
    val xAiApiKey: String = ""
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

    fun activeApiKey(): String {
        return keyFor(selectedProvider)
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
    }

    val settingsFlow: Flow<AppSettings> =
        context.settingsDataStore.data.map { prefs ->
            val legacyOpenRouterKey = prefs[Keys.API_KEY] ?: ""
            val provider = runCatching {
                ApiProvider.valueOf(
                    prefs[Keys.SELECTED_PROVIDER] ?: ApiProvider.OpenRouter.name
                )
            }.getOrDefault(ApiProvider.OpenRouter)

            val openRouterKey = prefs[Keys.OPENROUTER_API_KEY] ?: legacyOpenRouterKey

            val requestedModel = prefs[Keys.MODEL]
                ?: AiModels.defaultForProvider(provider).id

            val normalizedModel = AiModels.byIdOrNull(requestedModel)
                ?.takeIf { it.apiProvider == provider }
                ?.id
                ?: AiModels.defaultForProvider(provider).id

            AppSettings(
                apiKey = openRouterKey,
                model = normalizedModel,
                themeMode = runCatching {
                    AppThemeMode.valueOf(
                        prefs[Keys.THEME_MODE] ?: AppThemeMode.Auto.name
                    )
                }.getOrDefault(AppThemeMode.Auto),
                selectedProvider = provider,
                openRouterApiKey = openRouterKey,
                openAiApiKey = prefs[Keys.OPENAI_API_KEY] ?: "",
                anthropicApiKey = prefs[Keys.ANTHROPIC_API_KEY] ?: "",
                geminiApiKey = prefs[Keys.GEMINI_API_KEY] ?: "",
                groqApiKey = prefs[Keys.GROQ_API_KEY] ?: "",
                mistralApiKey = prefs[Keys.MISTRAL_API_KEY] ?: "",
                deepSeekApiKey = prefs[Keys.DEEPSEEK_API_KEY] ?: "",
                xAiApiKey = prefs[Keys.XAI_API_KEY] ?: ""
            )
        }

    suspend fun saveSettings(
        apiKey: String,
        model: String,
        themeMode: AppThemeMode
    ) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.SELECTED_PROVIDER] = ApiProvider.OpenRouter.name
            prefs[Keys.API_KEY] = apiKey.trim()
            prefs[Keys.OPENROUTER_API_KEY] = apiKey.trim()
            prefs[Keys.MODEL] = model.trim().ifBlank {
                AiModels.defaultForProvider(ApiProvider.OpenRouter).id
            }
            prefs[Keys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun saveAllSettings(
        selectedProvider: ApiProvider,
        model: String,
        themeMode: AppThemeMode,
        openRouterApiKey: String,
        openAiApiKey: String,
        anthropicApiKey: String,
        geminiApiKey: String,
        groqApiKey: String,
        mistralApiKey: String,
        deepSeekApiKey: String,
        xAiApiKey: String
    ) {
        val safeModel = AiModels.byIdOrNull(model)
            ?.takeIf { it.apiProvider == selectedProvider }
            ?.id
            ?: AiModels.defaultForProvider(selectedProvider).id

        context.settingsDataStore.edit { prefs ->
            prefs[Keys.SELECTED_PROVIDER] = selectedProvider.name
            prefs[Keys.MODEL] = safeModel
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
        }
    }

    suspend fun clearSettings() {
        context.settingsDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
