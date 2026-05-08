package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

@Composable
fun ChatScreen() {
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf("Mr. Robot online. Configure OpenRouter API key in Settings, then connect real chat logic.") }

    ScreenShell {
        Title("AI Chat")
        Subtitle("OpenRouter-ready chat workspace.")
        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                GlassCard(modifier = Modifier.padding(bottom = 10.dp)) {
                    Subtitle(msg)
                }
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            placeholder = { Text("Ask Mr. Robot...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        CyberButton("Send") {
            if (input.isNotBlank()) {
                messages.add("You: $input")
                messages.add("AI: MVP response placeholder. Next upgrade wires OpenRouter API call.")
                input = ""
            }
        }
    }
}
