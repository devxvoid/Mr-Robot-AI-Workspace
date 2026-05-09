package com.mrrobot.aiworkspace.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun ChatScreen() {

    ScreenShell {

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                PageTitle("AI Workspace")

                Subtitle(
                    "OpenRouter AI chat system."
                )
            }

            item {

                Panel(
                    title = "AI Status",
                    subtitle = "Core AI systems online."
                ) {

                    StatusPill(
                        text = "CONNECTED"
                    )

                    SoftText(
                        text =
                            "Streaming, markdown rendering, memory, and multi-session architecture are enabled."
                    )
                }
            }

            item {

                Panel(
                    title = "Workspace Ready"
                ) {

                    Text(
                        text =
                            "The AI runtime is initialized successfully."
                    )
                }
            }
        }
    }
}
