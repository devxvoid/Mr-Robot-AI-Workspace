package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.WorkspaceCapability
import com.mrrobot.aiworkspace.data.WorkspaceStat
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.AppCard
import com.mrrobot.aiworkspace.ui.components.BodyText
import com.mrrobot.aiworkspace.ui.components.CaptionText
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.ui.components.GroupTitle
import com.mrrobot.aiworkspace.ui.components.PrimaryTonalButton
import com.mrrobot.aiworkspace.ui.components.SectionHeader
import com.mrrobot.aiworkspace.ui.components.StatusChip
import com.mrrobot.aiworkspace.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    parentPadding: PaddingValues = PaddingValues()
) {
    val state by viewModel.uiState.collectAsState()
    val clipboard = LocalClipboardManager.current
    val profile = state.profile
    val scrollBehavior = TopAppBarDefaults
        .exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = { Text("Profile") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = mergedScreenPadding(innerPadding, parentPadding),
            verticalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            item {
                SectionHeader(
                    title = "Identity",
                    subtitle = "Workspace identity, status, capabilities, and export summary."
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(Modifier.height(4.dp))

                            BodyText(profile.title)
                            CaptionText("@${profile.handle}")

                            Spacer(Modifier.height(12.dp))

                            StatusChip(profile.status)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    BodyText(profile.principle)
                }
            }

            item {
                AppCard {
                    GroupTitle("Online Presence")
                    Spacer(Modifier.height(8.dp))
                    BodyText("GitHub: ${profile.github}")
                    BodyText("Website: ${profile.website}")
                    BodyText("X: ${profile.xProfile}")
                }
            }

            item {
                Text(
                    text = "Workspace Stats",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(state.stats) { stat ->
                StatCard(stat)
            }

            item {
                Text(
                    text = "Capabilities",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(state.capabilities) { capability ->
                CapabilityCard(capability)
            }

            item {
                AppCard {
                    GroupTitle("Export Workspace Profile")
                    Spacer(Modifier.height(4.dp))
                    BodyText("Generate a markdown summary of the current app identity and capabilities.")

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(GroupSpacing)
                    ) {
                        Button(
                            onClick = { viewModel.generateExport() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("Generate")
                        }

                        OutlinedButton(
                            onClick = { viewModel.clearExport() },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Clear")
                        }
                    }

                    if (state.exportText.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = state.exportText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        PrimaryTonalButton("Copy Export") {
                            clipboard.setText(AnnotatedString(state.exportText))
                            viewModel.markCopied()
                        }
                    }

                    if (state.copiedMessage.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        CaptionText(state.copiedMessage)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(stat: WorkspaceStat) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                BodyText(stat.description)
            }

            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CapabilityCard(capability: WorkspaceCapability) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = capability.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                BodyText(capability.description)
            }

            StatusChip(if (capability.enabled) "Enabled" else "Disabled")
        }
    }
}
