package com.mrrobot.aiworkspace

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import com.mrrobot.aiworkspace.data.AppSettings
import com.mrrobot.aiworkspace.data.AppThemeMode
import com.mrrobot.aiworkspace.data.SettingsStore
import com.mrrobot.aiworkspace.navigation.AppNavGraph
import com.mrrobot.aiworkspace.ui.screens.SplashScreen
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

        setContent {
            val settings by settingsStore.settingsFlow.collectAsState(initial = initialSettings)
            val systemDark = isSystemInDarkTheme()
            var showSplash by remember { mutableStateOf(true) }
            val view = LocalView.current

            val isDarkUi = isDarkUi(
                themeMode = settings.themeMode,
                systemDark = systemDark
            )

            SideEffect {
                val barColor = if (showSplash) {
                    if (isDarkUi) Color.BLACK else Color.WHITE
                } else {
                    if (isDarkUi) Color.rgb(3, 7, 18) else Color.rgb(248, 250, 252)
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

            if (showSplash) {
                SplashScreen(
                    themeMode = settings.themeMode,
                    systemDark = systemDark,
                    onFinished = { showSplash = false }
                )
            } else {
                MrRobotTheme(themeMode = settings.themeMode) {
                    AppNavGraph()
                }
            }
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
