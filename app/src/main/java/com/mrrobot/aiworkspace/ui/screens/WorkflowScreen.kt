package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.runtime.Composable
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun WorkflowScreen() {

    ScreenShell {

        Panel(
            title = "Workflow",
            subtitle = "Workflow module."
        ) {

            SoftText(
                text = "Workflow system initialized."
            )
        }
    }
}
