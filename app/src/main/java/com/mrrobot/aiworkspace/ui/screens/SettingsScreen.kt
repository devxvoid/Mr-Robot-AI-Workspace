package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun SettingsScreen() {
    var apiKey by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("openai/gpt-4o-mini") }

    ScreenShell {
        Title("Settings")
        Subtitle("Configure AI provider and workspace preferences.")
        Spacer(Modifier.height(16.dp))

        GlassCard {
            Subtitle("OpenRouter API Key")
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                placeholder = { Text("sk-or-v1-...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Subtitle("Model")
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            CyberButton("Save Settings") {}
        }
    }
}
