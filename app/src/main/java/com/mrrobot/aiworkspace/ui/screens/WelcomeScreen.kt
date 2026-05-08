package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun WelcomeScreen(nav: NavController) {
    ScreenShell {
        Spacer(Modifier.height(24.dp))
        Title("Mr. Robot AI Workspace")
        Subtitle("Premium cyberpunk AI workspace generated from your Google Stitch design.")
        Spacer(Modifier.height(24.dp))

        GlassCard {
            Title("Command Center")
            Subtitle("Chat with AI, manage agents, build workflows, inspect files, and configure models.")
            Spacer(Modifier.height(18.dp))
            CyberButton("Launch AI Chat") { nav.navigate(Route.Chat.path) }
            Spacer(Modifier.height(10.dp))
            CyberButton("Open Agents") { nav.navigate(Route.Agents.path) }
            Spacer(Modifier.height(10.dp))
            CyberButton("Workflow Builder") { nav.navigate(Route.Workflow.path) }
        }

        Spacer(Modifier.height(16.dp))

        GlassCard {
            Subtitle("Stitch reference ZIP is kept in your repo root as the source design asset.")
        }
    }
}
