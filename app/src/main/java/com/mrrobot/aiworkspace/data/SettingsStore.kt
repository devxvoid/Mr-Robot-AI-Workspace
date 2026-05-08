package com.mrrobot.aiworkspace.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "mr_robot_settings")

data class AppSettings(
    val apiKey: String = "",
    val model: String = "openai/gpt-4o-mini"
)

class SettingsStore(private val context: Context) {

    private object Keys {
        val API_KEY = stringPreferencesKey("openrouter_api_key")
        val MODEL = stringPreferencesKey("selected_model")
    }

    val settingsFlow: Flow<AppSettings> =
        context.settingsDataStore.data.map { prefs ->
            AppSettings(
                apiKey = prefs[Keys.API_KEY] ?: "",
                model = prefs[Keys.MODEL] ?: "openai/gpt-4o-mini"
            )
        }

    suspend fun saveSettings(apiKey: String, model: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.API_KEY] = apiKey.trim()
            prefs[Keys.MODEL] = model.trim().ifBlank { "openai/gpt-4o-mini" }
        }
    }

    suspend fun clearSettings() {
        context.settingsDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
