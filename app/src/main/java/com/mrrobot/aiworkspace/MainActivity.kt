package com.mrrobot.aiworkspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mrrobot.aiworkspace.data.AppSettings
import com.mrrobot.aiworkspace.data.SettingsStore
import com.mrrobot.aiworkspace.navigation.AppNavGraph
import com.mrrobot.aiworkspace.ui.theme.MrRobotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsStore = SettingsStore(applicationContext)

        setContent {
            val settings by settingsStore.settingsFlow.collectAsState(
                initial = AppSettings()
            )

            MrRobotTheme(themeMode = settings.themeMode) {
                AppNavGraph()
            }
        }
    }
}
