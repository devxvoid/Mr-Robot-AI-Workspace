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
    Light
}

data class AppSettings(
    val apiKey: String = "",
    val model: String = "openai/gpt-4o-mini",
    val themeMode: AppThemeMode = AppThemeMode.Auto
)

class SettingsStore(private val context: Context) {

    private object Keys {
        val API_KEY = stringPreferencesKey("openrouter_api_key")
        val MODEL = stringPreferencesKey("selected_model")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val settingsFlow: Flow<AppSettings> =
        context.settingsDataStore.data.map { prefs ->
            AppSettings(
                apiKey = prefs[Keys.API_KEY] ?: "",
                model = prefs[Keys.MODEL] ?: "openai/gpt-4o-mini",
                themeMode = runCatching {
                    AppThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: AppThemeMode.Auto.name)
                }.getOrDefault(AppThemeMode.Auto)
            )
        }

    suspend fun saveSettings(
        apiKey: String,
        model: String,
        themeMode: AppThemeMode
    ) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.API_KEY] = apiKey.trim()
            prefs[Keys.MODEL] = model.trim().ifBlank { "openai/gpt-4o-mini" }
            prefs[Keys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun clearSettings() {
        context.settingsDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
