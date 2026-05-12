package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.data.AiModels
import com.mrrobot.aiworkspace.data.ApiProvider
import com.mrrobot.aiworkspace.data.AppSettings
import com.mrrobot.aiworkspace.data.AppThemeMode
import com.mrrobot.aiworkspace.data.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
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
    val savedMessage: String = "",
    val isLoaded: Boolean = false
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

    val configuredProviders: List<ApiProvider>
        get() = ApiProvider.values().filter { keyFor(it).isNotBlank() }
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val store = SettingsStore(application.applicationContext)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            store.settingsFlow.collect { settings: AppSettings ->
                _uiState.value = _uiState.value.copy(
                    apiKey = settings.apiKey,
                    model = settings.model,
                    themeMode = settings.themeMode,
                    selectedProvider = settings.selectedProvider,
                    openRouterApiKey = settings.openRouterApiKey,
                    openAiApiKey = settings.openAiApiKey,
                    anthropicApiKey = settings.anthropicApiKey,
                    geminiApiKey = settings.geminiApiKey,
                    groqApiKey = settings.groqApiKey,
                    mistralApiKey = settings.mistralApiKey,
                    deepSeekApiKey = settings.deepSeekApiKey,
                    xAiApiKey = settings.xAiApiKey,
                    isLoaded = true
                )
            }
        }
    }

    fun updateThemeMode(value: AppThemeMode) {
        _uiState.value = _uiState.value.copy(
            themeMode = value,
            savedMessage = ""
        )
    }

    fun updateModel(value: String) {
        _uiState.value = _uiState.value.copy(
            model = value,
            savedMessage = ""
        )
    }

    fun updateSelectedProvider(value: ApiProvider) {
        val current = _uiState.value
        val safeModel = AiModels.defaultForProvider(value).id

        _uiState.value = current.copy(
            selectedProvider = value,
            model = safeModel,
            savedMessage = ""
        )
    }

    fun updateApiKey(value: String) {
        val current = _uiState.value

        _uiState.value = when (current.selectedProvider) {
            ApiProvider.OpenRouter -> current.copy(apiKey = value, openRouterApiKey = value, savedMessage = "")
            ApiProvider.OpenAI -> current.copy(openAiApiKey = value, savedMessage = "")
            ApiProvider.Anthropic -> current.copy(anthropicApiKey = value, savedMessage = "")
            ApiProvider.Gemini -> current.copy(geminiApiKey = value, savedMessage = "")
            ApiProvider.Groq -> current.copy(groqApiKey = value, savedMessage = "")
            ApiProvider.Mistral -> current.copy(mistralApiKey = value, savedMessage = "")
            ApiProvider.DeepSeek -> current.copy(deepSeekApiKey = value, savedMessage = "")
            ApiProvider.XAI -> current.copy(xAiApiKey = value, savedMessage = "")
        }
    }

    fun saveProviderConfiguration(
        provider: ApiProvider,
        model: String,
        apiKey: String,
        activate: Boolean
    ) {
        viewModelScope.launch {
            val current = applyProviderKey(
                state = _uiState.value,
                provider = provider,
                apiKey = apiKey
            )

            val targetProvider = if (activate) provider else current.selectedProvider
            val targetModel = if (activate) model else current.model

            store.saveAllSettings(
                selectedProvider = targetProvider,
                model = targetModel,
                themeMode = current.themeMode,
                openRouterApiKey = current.openRouterApiKey,
                openAiApiKey = current.openAiApiKey,
                anthropicApiKey = current.anthropicApiKey,
                geminiApiKey = current.geminiApiKey,
                groqApiKey = current.groqApiKey,
                mistralApiKey = current.mistralApiKey,
                deepSeekApiKey = current.deepSeekApiKey,
                xAiApiKey = current.xAiApiKey
            )

            _uiState.value = current.copy(
                selectedProvider = targetProvider,
                model = targetModel,
                savedMessage = if (activate) {
                    "${provider.displayName} saved and activated"
                } else {
                    "${provider.displayName} key saved"
                }
            )
        }
    }

    fun save() {
        viewModelScope.launch {
            val current = _uiState.value

            store.saveAllSettings(
                selectedProvider = current.selectedProvider,
                model = current.model,
                themeMode = current.themeMode,
                openRouterApiKey = current.openRouterApiKey,
                openAiApiKey = current.openAiApiKey,
                anthropicApiKey = current.anthropicApiKey,
                geminiApiKey = current.geminiApiKey,
                groqApiKey = current.groqApiKey,
                mistralApiKey = current.mistralApiKey,
                deepSeekApiKey = current.deepSeekApiKey,
                xAiApiKey = current.xAiApiKey
            )

            _uiState.value = current.copy(
                savedMessage = "Settings saved successfully"
            )
        }
    }

    fun saveThemeOnly(themeMode: AppThemeMode) {
        viewModelScope.launch {
            val current = _uiState.value.copy(themeMode = themeMode)

            store.saveAllSettings(
                selectedProvider = current.selectedProvider,
                model = current.model,
                themeMode = current.themeMode,
                openRouterApiKey = current.openRouterApiKey,
                openAiApiKey = current.openAiApiKey,
                anthropicApiKey = current.anthropicApiKey,
                geminiApiKey = current.geminiApiKey,
                groqApiKey = current.groqApiKey,
                mistralApiKey = current.mistralApiKey,
                deepSeekApiKey = current.deepSeekApiKey,
                xAiApiKey = current.xAiApiKey
            )

            _uiState.value = current.copy(
                savedMessage = "Theme updated"
            )
        }
    }

    fun clear() {
        viewModelScope.launch {
            store.clearSettings()

            _uiState.value = SettingsUiState(
                savedMessage = "Settings cleared",
                isLoaded = true
            )
        }
    }

    private fun applyProviderKey(
        state: SettingsUiState,
        provider: ApiProvider,
        apiKey: String
    ): SettingsUiState {
        return when (provider) {
            ApiProvider.OpenRouter -> state.copy(apiKey = apiKey, openRouterApiKey = apiKey)
            ApiProvider.OpenAI -> state.copy(openAiApiKey = apiKey)
            ApiProvider.Anthropic -> state.copy(anthropicApiKey = apiKey)
            ApiProvider.Gemini -> state.copy(geminiApiKey = apiKey)
            ApiProvider.Groq -> state.copy(groqApiKey = apiKey)
            ApiProvider.Mistral -> state.copy(mistralApiKey = apiKey)
            ApiProvider.DeepSeek -> state.copy(deepSeekApiKey = apiKey)
            ApiProvider.XAI -> state.copy(xAiApiKey = apiKey)
        }
    }
}
