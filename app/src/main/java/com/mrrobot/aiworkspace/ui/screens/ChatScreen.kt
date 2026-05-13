package com.mrrobot.aiworkspace.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.R
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.NeonCyan
import com.mrrobot.aiworkspace.ui.components.NeonPurple
import com.mrrobot.aiworkspace.ui.components.Panel
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.SoftText
import com.mrrobot.aiworkspace.ui.components.Subtitle
import com.mrrobot.aiworkspace.ui.components.Title
import com.mrrobot.aiworkspace.viewmodel.ChatUiMessage
import com.mrrobot.aiworkspace.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()
                .trim()

            if (spokenText.isNotBlank()) {
                val currentInput = state.input.trim()

                val newInput = if (currentInput.isBlank()) {
                    spokenText
                } else {
                    "$currentInput $spokenText"
                }

                viewModel.updateInput(newInput)
            }
        }
    }

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
                Subtitle("Attach files, speak prompts, analyze screenshots, and coordinate Android development work.")
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
                Subtitle("No active model configured. Open Settings, add an API key, then save settings.")
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
            onMicClick = {
                launchSpeechInput(
                    context = context,
                    launcher = speechLauncher::launch
                )
            },
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
    onMicClick: () -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onRegenerate: () -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(18.dp),
                color = NeonCyan.copy(alpha = 0.14f),
                border = BorderStroke(
                    width = 1.dp,
                    color = NeonCyan.copy(alpha = 0.35f)
                )
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lucide_plus),
                        contentDescription = "Attach files",
                        tint = NeonCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(18.dp),
                color = NeonCyan.copy(alpha = 0.14f),
                border = BorderStroke(
                    width = 1.dp,
                    color = NeonCyan.copy(alpha = 0.35f)
                )
            ) {
                IconButton(onClick = onMicClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lucide_mic),
                        contentDescription = "Voice input",
                        tint = NeonCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = { Text("Ask Mr. Robot...") },
                modifier = Modifier.weight(1f),
                minLines = 1,
                maxLines = 5,
                shape = RoundedCornerShape(18.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
    }

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

private fun launchSpeechInput(
    context: Context,
    launcher: (Intent) -> Unit
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Mr. Robot")
    }

    runCatching {
        launcher(intent)
    }.onFailure {
        Toast.makeText(
            context,
            "Voice input is not available on this device.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
