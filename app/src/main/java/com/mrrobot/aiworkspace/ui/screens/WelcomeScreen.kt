package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            item {
                PremiumHeader(
                    title = "Mr. Robot AI Workspace",
                    subtitle = "A premium Android command center for AI chat, agents, workflows, terminal logs, files, marketplace modules, and OpenRouter models.",
                    badge = "MVP"
                )

                Spacer(Modifier.height(18.dp))

                GlassCard {
                    Title("Command Center")
                    Subtitle("Your Stitch design has evolved into a real Kotlin + Jetpack Compose MVP foundation.")
                    Spacer(Modifier.height(16.dp))

                    CyberButton("Launch AI Chat") {
                        nav.navigate(Route.Chat.path)
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { nav.navigate(Route.Agents.path) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Agent System")
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { nav.navigate(Route.Workflow.path) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Build Workflow")
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "Screens",
                            value = "10",
                            description = "Full MVP navigation"
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "AI",
                            value = "OR",
                            description = "OpenRouter ready"
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "Agents",
                            value = "5",
                            description = "Role-based prompts"
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "CI/CD",
                            value = "APK",
                            description = "GitHub builds"
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                GlassCard {
                    Title("Design Source")
                    Subtitle("Keep stitch_mr._robot_ai_workspace.zip in the repo root. It remains your official Google Stitch reference asset.")
                }
            }
        }
    }
}
