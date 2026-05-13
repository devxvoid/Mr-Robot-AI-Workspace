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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.R
import com.mrrobot.aiworkspace.viewmodel.ChatUiMessage
import com.mrrobot.aiworkspace.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.util.Locale

private val BgTop = Color(0xFF0F0D0C)
private val BgBottom = Color(0xFF141210)

private val CardBg = Color(0xFF1A1715)
private val CardBorder = Color(0x22F4B183)

private val SoftOutline = Color(0x22F4B183)
private val SoftText = Color(0xFFE8DCD4)
private val SecondaryText = Color(0xFFA89B91)
private val Accent = Color(0xFFF4B183)
private val AccentStrong = Color(0xFFF0AE82)
private val AccentSoftFill = Color(0x1AF4B183)

private val InputBg = Color(0xFF131110)
private val AssistantBubble = Color(0xFF161412)
private val UserBubble = Color(0x1A18D7F0)
private val ErrorBg = Color(0xFF3D1515)
private val ErrorText = Color(0xFFFFD4D4)

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
                val merged = if (state.input.isBlank()) {
                    spokenText
                } else {
                    state.input + " " + spokenText
                }
                viewModel.updateInput(merged)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BgTop, BgBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp)
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            ChatHeader(
                onClear = { viewModel.clearChat() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AssistantStatusCard(
                isConfigured = state.apiKey.isNotBlank(),
                model = state.model,
                onSetup = {
                    // keep hook if you wire navigation later
                },
                userMessages = state.messages.count { it.role == "user" },
                queuedFiles = 0,
                readableFiles = 0,
                visionImages = 0
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = state.messages,
                    key = { it.id }
                ) { message ->
                    ChatBubble(message = message)
                }

                if (state.messages.isEmpty()) {
                    item {
                        ChatBubble(
                            message = ChatUiMessage(
                                id = 0L,
                                role = "assistant",
                                content = "Mr. Robot online. Add and activate any supported AI provider in Settings, then send a message."
                            )
                        )
                    }
                }

                if (state.isLoading) {
                    item {
                        ThinkingBubble()
                    }
                }
            }

            if (state.error.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                ErrorPanel(
                    message = state.error,
                    onRetry = { viewModel.retryLast() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            PromptComposer(
                input = state.input,
                isLoading = state.isLoading,
                canRegenerate = state.messages.any { it.role == "user" },
                onInputChange = viewModel::updateInput,
                onMicClick = {
                    launchSpeechInput(
                        context = context,
                        launcher = speechLauncher::launch
                    )
                },
                onAttachClick = {
                    // attach hook for your file picker
                },
                onSend = { viewModel.send() },
                onStop = { viewModel.stopGeneration() },
                onRegenerate = { viewModel.regenerateLastAnswer() }
            )
        }
    }
}

@Composable
private fun ChatHeader(
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "AI Chat",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Attach files, speak prompts, and coordinate development work.",
                color = SecondaryText,
                style = MaterialTheme.typography.bodySmall
            )
        }

        TextButton(
            onClick = onClear
        ) {
            Text(
                text = "Clear",
                color = Accent,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun AssistantStatusCard(
    isConfigured: Boolean,
    model: String,
    onSetup: () -> Unit,
    userMessages: Int,
    queuedFiles: Int,
    readableFiles: Int,
    visionImages: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Assistant Status",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isConfigured) "API key configured  •  Ready" else "No active model configured",
                        color = if (isConfigured) SoftText else SecondaryText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (!isConfigured) {
                    OutlinedButton(
                        onClick = onSetup,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Accent),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Accent
                        )
                    ) {
                        Text(
                            text = "SETUP",
                            color = Accent,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, Color(0x15FFFFFF))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusRow("Model", model)
                    StatusRow("User messages", userMessages.toString())
                    StatusRow("Queued files", queuedFiles.toString())
                    StatusRow("Readable files", readableFiles.toString())
                    StatusRow("Vision images", visionImages.toString())
                }
            }
        }
    }
}

@Composable
private fun StatusRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = SecondaryText,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun ChatBubble(
    message: ChatUiMessage
) {
    val clipboard = LocalClipboardManager.current
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.86f else 0.96f),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) UserBubble else AssistantBubble
            ),
            border = BorderStroke(
                1.dp,
                if (isUser) Color(0x2218D7F0) else Color(0x15FFFFFF)
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = if (isUser) "You" else "Mr. Robot",
                        color = if (isUser) Color(0xFF6DEBFF) else Color(0xFFA77BFF),
                        style = MaterialTheme.typography.labelMedium
                    )

                    Text(
                        text = "Copy",
                        color = SecondaryText,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.clickable {
                            clipboard.setText(AnnotatedString(message.content))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                MessageText(message.content)
            }
        }
    }
}

@Composable
private fun MessageText(content: String) {
    val looksLikeCode =
        content.contains("```") ||
            content.lines().any {
                val line = it.trim()
                line.startsWith("fun ") ||
                    line.startsWith("class ") ||
                    line.startsWith("val ") ||
                    line.startsWith("var ") ||
                    line.startsWith("import ") ||
                    line.startsWith("package ")
            }

    if (looksLikeCode) {
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
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = Accent
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Mr. Robot is thinking...",
                color = SoftText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ErrorPanel(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ErrorBg),
        border = BorderStroke(1.dp, Color(0x44FF8A8A))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "Request failed",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                color = ErrorText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onRetry,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0x55FFB6B6))
            ) {
                Text(
                    text = "Retry last prompt",
                    color = Color(0xFFFFD4D4),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun PromptComposer(
    input: String,
    isLoading: Boolean,
    canRegenerate: Boolean,
    onInputChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onAttachClick: () -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onRegenerate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallActionButton(
                    iconRes = R.drawable.ic_lucide_plus,
                    contentDescription = "Attach",
                    onClick = onAttachClick
                )

                SmallActionButton(
                    iconRes = R.drawable.ic_lucide_mic,
                    contentDescription = "Voice input",
                    onClick = onMicClick
                )

                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Ask Mr. Robot...",
                            color = SecondaryText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Accent,
                        unfocusedIndicatorColor = SoftOutline,
                        cursorColor = Accent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = SecondaryText,
                        unfocusedPlaceholderColor = SecondaryText
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (isLoading) onStop() else onSend()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentStrong,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isLoading) "Stop" else "Send",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                OutlinedButton(
                    onClick = onRegenerate,
                    enabled = canRegenerate && !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SoftOutline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SecondaryText,
                        disabledContentColor = SecondaryText.copy(alpha = 0.45f)
                    )
                ) {
                    Text(
                        text = "Regenerate",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallActionButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = AccentSoftFill,
        border = BorderStroke(1.dp, SoftOutline)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = Accent,
                modifier = Modifier.size(22.dp)
            )
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
