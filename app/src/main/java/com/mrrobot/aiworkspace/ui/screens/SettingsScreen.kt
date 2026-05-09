package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mrrobot.aiworkspace.data.AppThemeMode
import com.mrrobot.aiworkspace.navigation.AppRoutes
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
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
                Subtitle("Theme, AI configuration, and extra workspace modules.")
            }

            item {
                Panel(
                    title = "Theme",
                    subtitle = "Auto, Dark, or Light mode."
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

                    Spacer(modifier = Modifier.height(10.dp))

                    PrimaryButton(
                        text = "Save Settings",
                        onClick = {
                            viewModel.save()
                        }
                    )
                }
            }

            item {
                Panel(
                    title = "OpenRouter",
                    subtitle = "API key and selected model."
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

                    if (state.savedMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Subtitle(state.savedMessage)
                    }
                }
            }

            item {
                Panel(
                    title = "Workspace Modules",
                    subtitle = "Moved from bottom navigation."
                ) {
                    SecondaryButton(
                        text = "Agents",
                        onClick = {
                            navController.navigate(AppRoutes.Agents.route)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SecondaryButton(
                        text = "Workflow Builder",
                        onClick = {
                            navController.navigate(AppRoutes.Workflow.route)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SecondaryButton(
                        text = "Terminal",
                        onClick = {
                            navController.navigate(AppRoutes.Terminal.route)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SecondaryButton(
                        text = "File Manager",
                        onClick = {
                            navController.navigate(AppRoutes.Files.route)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SecondaryButton(
                        text = "Profile",
                        onClick = {
                            navController.navigate(AppRoutes.Profile.route)
                        }
                    )
                }
            }
        }
    }
}
