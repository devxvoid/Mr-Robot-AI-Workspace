package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun WelcomeScreen(nav: NavController) {
    ScreenShell {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        PageTitle("Mr. Robot")
                        PageTitle("AI Workspace")
                        Spacer(Modifier.height(6.dp))
                        Subtitle("High-end Android command center for AI chat, agents, workflows, terminal logs, files, marketplace modules, and OpenRouter models.")
                    }

                    StatusPill("MVP")
                }

                Spacer(Modifier.height(16.dp))

                GlassCard {
                    Text(
                        text = "// HELLO, FRIEND",
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Title("System Dashboard")
                    Subtitle("All core modules are wired into a stable, Play Store-ready Compose shell.")
                    Spacer(Modifier.height(16.dp))

                    CyberButton("Launch AI Chat") {
                        nav.navigate(Route.Chat.path)
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlineAction("Open Agent System") {
                        nav.navigate(Route.Agents.path)
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlineAction("Build Workflow") {
                        nav.navigate(Route.Workflow.path)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PremiumMetric(
                        label = "AI",
                        value = "OR",
                        description = "OpenRouter ready",
                        modifier = Modifier.weight(1f)
                    )
                    PremiumMetric(
                        label = "Agents",
                        value = "5",
                        description = "Role prompts",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PremiumMetric(
                        label = "Flow",
                        value = "2",
                        description = "Pipelines",
                        modifier = Modifier.weight(1f)
                    )
                    PremiumMetric(
                        label = "Build",
                        value = "APK",
                        description = "GitHub CI",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                GlassCard {
                    Title("Design Reference")
                    Subtitle("The UI direction follows the uploaded Mr. Robot markdown reference: fixed nav, persistent theme, terminal feel, neon green/cyan accents, safe touch targets, and polished app-shell behavior.")
                }
            }
        }
    }
}
