package com.mrrobot.aiworkspace

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import com.mrrobot.aiworkspace.data.AppSettings
import com.mrrobot.aiworkspace.data.AppThemeMode
import com.mrrobot.aiworkspace.data.SettingsStore
import com.mrrobot.aiworkspace.navigation.AppNavGraph
import com.mrrobot.aiworkspace.ui.theme.MrRobotTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsStore = SettingsStore(applicationContext)

        val initialSettings = runCatching {
            runBlocking(Dispatchers.IO) {
                settingsStore.settingsFlow.first()
            }
        }.getOrDefault(AppSettings())

        applySystemBars(
            themeMode = initialSettings.themeMode,
            systemDark = true
        )

        setContent {
            val settings by settingsStore.settingsFlow.collectAsState(
                initial = initialSettings
            )

            val systemDark = isSystemInDarkTheme()
            val view = LocalView.current

            SideEffect {
                val isDarkUi = isDarkUi(
                    themeMode = settings.themeMode,
                    systemDark = systemDark
                )

                val barColor = if (isDarkUi) {
                    Color.rgb(3, 7, 18)
                } else {
                    Color.rgb(248, 250, 252)
                }

                window.statusBarColor = barColor
                window.navigationBarColor = barColor

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    window.navigationBarDividerColor = barColor
                }

                val controller = WindowInsetsControllerCompat(window, view)
                controller.isAppearanceLightStatusBars = !isDarkUi
                controller.isAppearanceLightNavigationBars = !isDarkUi
            }

            MrRobotTheme(themeMode = settings.themeMode) {
                AppNavGraph()
            }
        }
    }

    private fun applySystemBars(
        themeMode: AppThemeMode,
        systemDark: Boolean
    ) {
        val isDarkUi = isDarkUi(
            themeMode = themeMode,
            systemDark = systemDark
        )

        val barColor = if (isDarkUi) {
            Color.rgb(3, 7, 18)
        } else {
            Color.rgb(248, 250, 252)
        }

        window.statusBarColor = barColor
        window.navigationBarColor = barColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = barColor
        }
    }

    private fun isDarkUi(
        themeMode: AppThemeMode,
        systemDark: Boolean
    ): Boolean {
        return when (themeMode) {
            AppThemeMode.Auto -> systemDark
            AppThemeMode.Dark -> true
            AppThemeMode.Light -> false
            AppThemeMode.Cyberpunk -> false
            AppThemeMode.Hacker -> true
        }
    }
}
