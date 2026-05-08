package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun FileManagerScreen() {
    val files = listOf("BUILD_PROMPT.md", "stitch_mr_robot_ai_workspace.zip", "build.gradle.kts", "MainActivity.kt")

    ScreenShell {
        Title("File Manager")
        Subtitle("Project assets and generated files.")
        Spacer(Modifier.height(16.dp))
        files.forEach {
            GlassCard(modifier = Modifier.padding(bottom = 12.dp)) {
                Subtitle("📄 $it")
            }
        }
    }
}
