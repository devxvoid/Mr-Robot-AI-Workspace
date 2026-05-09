package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun AgentsScreen() {

    ScreenShell {

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                PageTitle("Agents")

                Subtitle(
                    "Manage AI agents and workflows."
                )
            }

            item {

                Panel(
                    title = "Sherlock",
                    subtitle = "Android engineering agent."
                ) {

                    StatusPill(
                        text = "ACTIVE"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Production Android development agent for Compose, architecture, debugging, and optimization."
                    )
                }
            }

            item {

                Panel(
                    title = "Future Runtime"
                ) {

                    SoftText(
                        text =
                            "Multi-agent orchestration engine coming next."
                    )
                }
            }
        }
    }
}
