package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ScreenShell {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Title("Settings")
            Subtitle("Configure AI provider and workspace preferences.")
            Spacer(Modifier.height(16.dp))

            GlassCard {
                Subtitle("OpenRouter API Key")

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.apiKey,
                    onValueChange = viewModel::updateApiKey,
                    placeholder = { Text("sk-or-v1-...") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Subtitle("Model")

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.model,
                    onValueChange = viewModel::updateModel,
                    placeholder = { Text("openai/gpt-4o-mini") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                CyberButton("Save Settings") {
                    viewModel.save()
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { viewModel.clear() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Settings")
                }

                if (state.savedMessage.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Subtitle(state.savedMessage)
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassCard {
                Title("Supported Models")
                Spacer(Modifier.height(8.dp))
                Subtitle("openai/gpt-4o-mini")
                Subtitle("google/gemini-2.0-flash-001")
                Subtitle("anthropic/claude-3.5-sonnet")
                Subtitle("deepseek/deepseek-chat")
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}
