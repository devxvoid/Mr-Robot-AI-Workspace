package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun WelcomeScreen(nav: NavController) {
    ScreenShell {

        PageTitle("Mr. Robot AI Workspace")

        Subtitle(
            "A premium Android AI workspace with agents, workflows, terminals, tools, and OpenRouter integration."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Panel(
            title = "Command Center",
            subtitle = "Professional AI workflow control system."
        ) {

            PrimaryButton(
                text = "Launch AI Chat",
                onClick = {
                    nav.navigate(Route.Chat.path)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Open Agent System",
                onClick = {
                    nav.navigate(Route.Agents.path)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Build Workflow",
                onClick = {
                    nav.navigate(Route.Workflow.path)
                }
            )
        }
    }
}
