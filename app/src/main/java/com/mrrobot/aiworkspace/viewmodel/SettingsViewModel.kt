package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
    val savedMessage: String = "",
    val isLoaded: Boolean = false
)

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
                    isLoaded = true
                )
            }
        }
    }

    fun updateApiKey(value: String) {
        _uiState.value = _uiState.value.copy(
            apiKey = value,
            savedMessage = ""
        )
    }

    fun updateModel(value: String) {
        _uiState.value = _uiState.value.copy(
            model = value,
            savedMessage = ""
        )
    }

    fun updateThemeMode(value: AppThemeMode) {
        _uiState.value = _uiState.value.copy(
            themeMode = value,
            savedMessage = ""
        )
    }

    fun save() {
        viewModelScope.launch {
            val current = _uiState.value

            store.saveSettings(
                apiKey = current.apiKey,
                model = current.model,
                themeMode = current.themeMode
            )

            _uiState.value = current.copy(
                savedMessage = "Settings saved successfully"
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
}
