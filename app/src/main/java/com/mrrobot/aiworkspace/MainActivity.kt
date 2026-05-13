package com.mrrobot.aiworkspace

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mrrobot.aiworkspace.data.AppSettings
import com.mrrobot.aiworkspace.data.SettingsStore
import com.mrrobot.aiworkspace.navigation.AppNavGraph
import com.mrrobot.aiworkspace.ui.screens.SplashScreen
import com.mrrobot.aiworkspace.ui.theme.MrRobotTheme
import com.mrrobot.aiworkspace.ui.theme.isDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Render into the entire window so the M3 Scaffold can apply
        // its own insets; individual TopAppBars then consume the
        // status bar inset natively.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

        val settingsStore = SettingsStore(applicationContext)

        val initialSettings = runCatching {
            runBlocking(Dispatchers.IO) {
                settingsStore.settingsFlow.first()
            }
        }.getOrDefault(AppSettings())

        setContent {
            val settings by settingsStore.settingsFlow.collectAsState(
                initial = initialSettings
            )

            val systemDark = isSystemInDarkTheme()
            val darkUi = settings.themeMode.isDark(systemDark)

            var showSplash by remember { mutableStateOf(true) }

            MrRobotTheme(themeMode = settings.themeMode) {
                val background = MaterialTheme.colorScheme.background
                val view = LocalView.current

                SideEffect {
                    // Keep system bars transparent so M3 surfaces
                    // bleed edge-to-edge. The Scaffold handles
                    // content insets via its padding.
                    window.statusBarColor = android.graphics.Color.TRANSPARENT
                    window.navigationBarColor = android.graphics.Color.TRANSPARENT

                    val controller = WindowInsetsControllerCompat(window, view)
                    controller.isAppearanceLightStatusBars = !darkUi
                    controller.isAppearanceLightNavigationBars = !darkUi

                    window.decorView.setBackgroundColor(background.toArgb())
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSplash) {
                        SplashScreen(
                            themeMode = settings.themeMode,
                            systemDark = systemDark,
                            onFinished = { showSplash = false }
                        )
                    } else {
                        AppNavGraph()
                    }
                }
            }
        }
    }
}
