package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrrobot.aiworkspace.ui.components.*

data class ChatMessage(
    val role: String,
    val message: String
)

@Composable
fun ChatScreen() {

    var input by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                "assistant",
                "Welcome to Mr. Robot AI Workspace."
            )
        )
    }

    ScreenShell {

        PageTitle("AI Chat")

        Subtitle("OpenRouter-ready AI workspace interface.")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(messages) { item ->

                GlassCard {

                    StatusPill(item.role.uppercase())

                    SoftText(item.message)
                }
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Prompt")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PrimaryButton(
            text = "Send",
            onClick = {

                if (input.isNotBlank()) {

                    messages.add(
                        ChatMessage(
                            "user",
                            input
                        )
                    )

                    messages.add(
                        ChatMessage(
                            "assistant",
                            "Response generated for: $input"
                        )
                    )

                    input = ""
                }
            }
        )
    }
}
