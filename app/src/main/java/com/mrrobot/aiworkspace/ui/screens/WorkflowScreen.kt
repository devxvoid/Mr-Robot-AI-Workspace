package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun WorkflowScreen() {
    val steps = listOf("Receive prompt", "Analyze task", "Assign agents", "Generate code", "Build APK", "Upload artifact")

    ScreenShell {
        Title("Workflow Builder")
        Subtitle("Automation pipeline designer.")
        Spacer(Modifier.height(16.dp))
        steps.forEachIndexed { index, step ->
            GlassCard(modifier = Modifier.padding(bottom = 12.dp)) {
                Title("${index + 1}. $step")
                Subtitle("Mock workflow block for MVP.")
            }
        }
    }
}
