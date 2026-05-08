package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun TerminalScreen() {
    ScreenShell {
        Title("Live Terminal")
        Subtitle("Execution logs and command output.")
        Spacer(Modifier.height(16.dp))
        GlassCard {
            Subtitle("$ ./gradlew assembleDebug")
            Subtitle("> BUILD SUCCESSFUL")
            Subtitle("> APK uploaded as GitHub artifact")
            Subtitle("> Mr. Robot workspace ready")
        }
    }
}
