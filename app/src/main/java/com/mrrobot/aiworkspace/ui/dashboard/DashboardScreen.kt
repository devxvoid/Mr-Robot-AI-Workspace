package com.mrrobot.aiworkspace.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun DashboardScreen() {

    ScreenShell {

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                PageTitle("Mr. Robot AI")

                Subtitle(
                    "Premium AI workspace dashboard."
                )
            }

            item {

                Panel(
                    title = "Workspace Status",
                    subtitle = "Core systems online."
                ) {

                    StatusPill(
                        text = "ONLINE"
                    )

                    SoftText(
                        text =
                            "AI, memory, marketplace, and workspace modules are operational."
                    )
                }
            }

            item {

                Panel(
                    title = "Quick Actions"
                ) {

                    PrimaryButton(
                        text = "Launch AI Workspace",
                        onClick = {}
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    SecondaryButton(
                        text = "Open Marketplace",
                        onClick = {}
                    )
                }
            }

            item {

                Panel(
                    title = "System Overview"
                ) {

                    Text(
                        text = "Multi-session AI platform with persistent memory, markdown rendering, streaming responses, and premium UI.",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
