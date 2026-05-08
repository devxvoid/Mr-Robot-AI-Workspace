package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.WorkspaceCapability
import com.mrrobot.aiworkspace.data.WorkspaceStat
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboardManager.current
    val profile = state.profile

    ScreenShell {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                Title("Profile")
                Subtitle("Workspace identity, status, capabilities, and export summary.")
                Spacer(Modifier.height(14.dp))

                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.displayName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(6.dp))

                            Subtitle(profile.title)
                            Subtitle("@${profile.handle}")

                            Spacer(Modifier.height(10.dp))

                            AssistChip(
                                onClick = {},
                                label = { Text(profile.status) }
                            )
                        }

                        Text(
                            text = "⚡",
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Subtitle(profile.principle)
                }

                Spacer(Modifier.height(12.dp))

                GlassCard {
                    Title("Online Presence")
                    Spacer(Modifier.height(8.dp))
                    Subtitle("GitHub: ${profile.github}")
                    Subtitle("Website: ${profile.website}")
                    Subtitle("X: ${profile.xProfile}")
                }

                Spacer(Modifier.height(12.dp))

                Title("Workspace Stats")
                Spacer(Modifier.height(8.dp))
            }

            items(state.stats) { stat ->
                StatCard(stat)
            }

            item {
                Spacer(Modifier.height(12.dp))
                Title("Capabilities")
                Spacer(Modifier.height(8.dp))
            }

            items(state.capabilities) { capability ->
                CapabilityCard(capability)
            }

            item {
                Spacer(Modifier.height(12.dp))

                GlassCard {
                    Title("Export Workspace Profile")
                    Subtitle("Generate a markdown summary of the current app identity and capabilities.")

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.generateExport() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Generate")
                        }

                        OutlinedButton(
                            onClick = { viewModel.clearExport() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear")
                        }
                    }

                    if (state.exportText.isNotBlank()) {
                        Spacer(Modifier.height(14.dp))

                        Text(
                            text = state.exportText,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(Modifier.height(12.dp))

                        CyberButton("Copy Export") {
                            clipboard.setText(AnnotatedString(state.exportText))
                            viewModel.markCopied()
                        }
                    }

                    if (state.copiedMessage.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Subtitle(state.copiedMessage)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(stat: WorkspaceStat) {
    GlassCard(modifier = Modifier.padding(bottom = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stat.label,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Subtitle(stat.description)
            }

            Text(
                text = stat.value,
                color = NeonCyan,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CapabilityCard(capability: WorkspaceCapability) {
    GlassCard(modifier = Modifier.padding(bottom = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = capability.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Subtitle(capability.description)
            }

            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = if (capability.enabled) "Enabled" else "Disabled",
                        color = if (capability.enabled) NeonCyan else SoftText
                    )
                }
            )
        }
    }
}
