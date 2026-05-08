package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
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

    LaunchedEffect(state.messages.size, state.isLoading) {
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
            Column(modifier = Modifier.weight(1f)) {
                Title("AI Chat")
                Subtitle("OpenRouter-powered Mr. Robot assistant.")
            }

            TextButton(onClick = { viewModel.clearChat() }) {
                Text("Clear")
            }
        }

        Spacer(Modifier.height(8.dp))

        GlassCard {
            Subtitle("Model: ${state.model}")
            Spacer(Modifier.height(6.dp))

            if (state.apiKey.isBlank()) {
                Subtitle("API key missing. Open Settings and save your OpenRouter key.")
            } else {
                Subtitle("API key configured • Ready")
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
            items(
                items = state.messages,
                key = { it.id }
            ) { message ->
                ChatBubble(message = message)
            }

            if (state.isLoading) {
                item {
                    ThinkingBubble()
                }
            }
        }

        if (state.error.isNotBlank()) {
            ErrorPanel(
                message = state.error,
                onRetry = { viewModel.retryLast() }
            )
        }

        PromptInputBar(
            input = state.input,
            isLoading = state.isLoading,
            onInputChange = viewModel::updateInput,
            onSend = { viewModel.send() },
            onStop = { viewModel.stopGeneration() },
            onRegenerate = { viewModel.regenerateLastAnswer() },
            canRegenerate = state.messages.any { it.role == "user" }
        )
    }
}

@Composable
private fun ChatBubble(message: ChatUiMessage) {
    val clipboard = LocalClipboardManager.current
    val isUser = message.role == "user"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.88f else 0.96f)
                .background(
                    color = if (isUser) NeonCyan.copy(alpha = 0.17f) else Panel.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = if (isUser) 24.dp else 6.dp,
                        bottomEnd = if (isUser) 6.dp else 24.dp
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUser) "You" else "Mr. Robot",
                    color = if (isUser) NeonCyan else NeonPurple,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Copy",
                    color = SoftText,
                    modifier = Modifier.clickable {
                        clipboard.setText(AnnotatedString(message.content))
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            MessageText(message.content)
        }
    }
}

@Composable
private fun MessageText(content: String) {
    val isCodeLike =
        content.contains("```") ||
        content.lines().any {
            it.trim().startsWith("fun ") ||
            it.trim().startsWith("class ") ||
            it.trim().startsWith("val ") ||
            it.trim().startsWith("var ") ||
            it.trim().startsWith("import ") ||
            it.trim().startsWith("package ")
        }

    if (isCodeLike) {
        Text(
            text = content.replace("```", ""),
            color = Color.White,
            fontFamily = FontFamily.Monospace
        )
    } else {
        Text(
            text = content,
            color = Color.White
        )
    }
}

@Composable
private fun ThinkingBubble() {
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

@Composable
private fun ErrorPanel(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3B1111)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = message,
                color = Color(0xFFFFB4AB)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun PromptInputBar(
    input: String,
    isLoading: Boolean,
    canRegenerate: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onRegenerate: () -> Unit
) {
    OutlinedTextField(
        value = input,
        onValueChange = onInputChange,
        placeholder = { Text("Ask Mr. Robot...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 1,
        maxLines = 5
    )

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = {
                if (isLoading) onStop() else onSend()
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isLoading) Color(0xFFFFB020) else NeonCyan,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = if (isLoading) "Stop" else "Send",
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onRegenerate,
            enabled = canRegenerate && !isLoading,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Regenerate")
        }
    }
}
