package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun AgentsScreen() {

    ScreenShell {

        PageTitle("Agents")

        Subtitle(
            "Build specialized prompts and execution roles for your AI workspace."
        )

        Spacer(modifier = Modifier.height(20.dp))

        Panel(
            title = "Android Architect",
            subtitle = "Production-grade Kotlin + Compose agent."
        ) {

            StatusPill("ACTIVE")

            SoftText(
                "Builds scalable Android architectures, Compose UI systems, repositories, Gradle fixes, and workflow automation."
            )

            Spacer(modifier = Modifier.height(14.dp))

            PrimaryButton(
                text = "Generate Prompt",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = "Clear",
                onClick = {}
            )
        }
    }
}
