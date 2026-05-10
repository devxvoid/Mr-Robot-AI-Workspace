package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.navigation.Route
import com.mrrobot.aiworkspace.ui.components.CyberButton
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.PremiumHeader
import com.mrrobot.aiworkspace.ui.components.PremiumMetric
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.StatusPill
import com.mrrobot.aiworkspace.ui.components.Subtitle
import com.mrrobot.aiworkspace.ui.components.Title

@Composable
fun WelcomeScreen(nav: NavController) {
    ScreenShell {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            item {
                PremiumHeader(
                    title = "Mr. Robot AI Workspace",
                    subtitle = "A professional Android command center rebuilt around imported Stitch design assets, Cyber/Hacker themes, AI chat, agent workflows, terminal logs, marketplace tools, and OpenRouter models.",
                    badge = "STEP 21"
                )

                Spacer(Modifier.height(16.dp))

                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusPill("Cyber")
                        StatusPill("Hacker")
                        StatusPill("Assets")
                    }

                    Spacer(Modifier.height(14.dp))

                    Title("Imported Design System")
                    Subtitle("The Stitch ZIP assets are imported into the project, while Settings shows only Auto, Dark, Light, Cyber, and Hacker.")

                    Spacer(Modifier.height(14.dp))

                    CyberButton("Launch AI Chat") {
                        nav.navigate(Route.Chat.path)
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { nav.navigate(Route.Agents.path) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Open Agent System")
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { nav.navigate(Route.Workflow.path) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
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
                            value = "20+",
                            description = "Imported previews"
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "Themes",
                            value = "5",
                            description = "Auto/Dark/Light/Cyber/Hacker"
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "AI",
                            value = "OR",
                            description = "OpenRouter"
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        PremiumMetric(
                            label = "Agents",
                            value = "5",
                            description = "Role prompts"
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                GlassCard {
                    Title("Design Source")
                    Subtitle("The imported ZIP remains your design source. Cyber and Hacker themes are available in Settings without adding a separate Stitch theme option.")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
