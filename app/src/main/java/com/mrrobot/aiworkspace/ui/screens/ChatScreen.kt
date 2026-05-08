package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.ui.components.*
import com.mrrobot.aiworkspace.viewmodel.ChatUiMessage
import com.mrrobot.aiworkspace.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(state.messages.lastIndex)
            }
        }
    }

    ScreenShell {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Title("AI Chat")
                Subtitle("Real OpenRouter chat workspace.")
            }

            TextButton(onClick = { viewModel.clearChat() }) {
                Text("Clear")
            }
        }

        Spacer(Modifier.height(8.dp))

        GlassCard {
            Subtitle("Model: ${state.model}")
            if (state.apiKey.isBlank()) {
                Spacer(Modifier.height(4.dp))
                Subtitle("API key missing. Open Settings and save your OpenRouter key.")
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(state.messages) { message ->
                ChatBubble(message)
            }

            if (state.isLoading) {
                item {
                    GlassCard(modifier = Modifier.padding(bottom = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(12.dp))
                            Subtitle("Mr. Robot is thinking...")
                        }
                    }
                }
            }
        }

        if (state.error.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF3B1111)
                )
            ) {
                Text(
                    text = state.error,
                    modifier = Modifier.padding(14.dp),
                    color = Color(0xFFFFB4AB)
                )
            }
        }

        OutlinedTextField(
            value = state.input,
            onValueChange = viewModel::updateInput,
            placeholder = { Text("Ask Mr. Robot...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 1,
            maxLines = 5
        )

        Spacer(Modifier.height(8.dp))

        CyberButton(
            text = if (state.isLoading) "Generating..." else "Send",
            onClick = { viewModel.send() }
        )
    }
}

@Composable
private fun ChatBubble(message: ChatUiMessage) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.88f else 0.96f)
                .background(
                    color = if (isUser) NeonCyan.copy(alpha = 0.16f) else Panel.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomStart = if (isUser) 22.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 22.dp
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = if (isUser) "You" else "Mr. Robot",
                color = if (isUser) NeonCyan else NeonPurple,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = message.content,
                color = Color.White
            )
        }
    }
}
