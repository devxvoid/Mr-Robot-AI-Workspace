package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun WelcomeScreen() {

    ScreenShell {

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                PageTitle("Mr. Robot AI Workspace")

                Subtitle(
                    "Premium AI operating environment."
                )
            }

            item {

                Panel(
                    title = "Workspace",
                    subtitle = "System initialized successfully."
                ) {

                    StatusPill(
                        text = "ONLINE"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    SoftText(
                        text =
                            "AI systems, navigation, marketplace, and theme engine are operational."
                    )
                }
            }

            item {

                Panel(
                    title = "Features"
                ) {

                    SoftText(
                        text =
                            "• OpenRouter AI\n• Marketplace\n• Multi-session workspace\n• Streaming architecture\n• Persistent memory\n• Premium UI"
                    )
                }
            }
        }
    }
}
