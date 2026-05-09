package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun MarketplaceScreen() {

    ScreenShell {

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                PageTitle("Marketplace")

                Subtitle(
                    "Plugins, models, workflows, and AI tools."
                )
            }

            item {

                Panel(
                    title = "OpenRouter Models"
                ) {

                    StatusPill(
                        text = "CONNECTED"
                    )

                    SoftText(
                        text =
                            "Dynamic AI model marketplace integration ready."
                    )
                }
            }
        }
    }
}
