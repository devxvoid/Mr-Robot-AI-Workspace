package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.data.AppThemeMode
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ScreenShell {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                PageTitle("Settings")
                Subtitle("Configure OpenRouter, model, and workspace appearance.")
            }

            item {
                Panel(
                    title = "Theme Engine",
                    subtitle = "Auto follows your device theme. Dark and Light override it."
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppThemeMode.values().forEach { mode ->
                            FilterChip(
                                selected = state.themeMode == mode,
                                onClick = {
                                    viewModel.updateThemeMode(mode)
                                },
                                label = {
                                    Text(mode.name)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    StatusPill(
                        text = "CURRENT: ${state.themeMode.name}"
                    )
                }
            }

            item {
                Panel(
                    title = "OpenRouter",
                    subtitle = "API key and model are saved with DataStore."
                ) {
                    OutlinedTextField(
                        value = state.apiKey,
                        onValueChange = viewModel::updateApiKey,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("API Key") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.model,
                        onValueChange = viewModel::updateModel,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Model") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PrimaryButton(
                        text = "Save Settings",
                        onClick = {
                            viewModel.save()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SecondaryButton(
                        text = "Clear Settings",
                        onClick = {
                            viewModel.clear()
                        }
                    )

                    if (state.savedMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Subtitle(state.savedMessage)
                    }
                }
            }
        }
    }
}
