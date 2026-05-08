package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun AgentsScreen() {
    val agents = listOf(
        "Android Architect",
        "UI/UX Strategist",
        "Backend Engineer",
        "Debugging Specialist",
        "GitHub Actions Builder"
    )

    ScreenShell {
        Title("Agents")
        Subtitle("Multi-agent command dashboard.")
        Spacer(Modifier.height(16.dp))
        agents.forEach {
            GlassCard(modifier = Modifier.padding(bottom = 12.dp)) {
                Title(it)
                Subtitle("Status: ready • Role: specialized execution agent")
            }
        }
    }
}
