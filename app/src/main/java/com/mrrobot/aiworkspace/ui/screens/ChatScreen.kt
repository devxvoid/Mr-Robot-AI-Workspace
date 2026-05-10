package com.mrrobot.aiworkspace.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.Subtitle
import com.mrrobot.aiworkspace.ui.components.Title
import com.mrrobot.aiworkspace.viewmodel.ChatUiMessage
import com.mrrobot.aiworkspace.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

private val PromptSuggestions = listOf(
    "Improve my Android app UI professionally",
    "Fix this Gradle build error",
    "Create a GitHub Actions APK workflow",
    "Review my Jetpack Compose screen",
    "Plan the next MVP features"
)

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
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ChatHeader(
                    model = state.model,
                    apiKeyConfigured = state.apiKey.isNotBlank(),
                    messageCount = state.messages.count { it.role == "user" },
                    onClear = { viewModel.clearChat() }
                )
            }

            item {
                PromptSuggestionRow(
                    onSuggestionClick = viewModel::useSuggestion
                )
            }

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

            Spacer(Modifier.height(10.dp))
        }

        PromptInputPanel(
            input = state.input,
            isLoading = state.isLoading,
            canRegenerate = state.messages.any { it.role == "user" },
            onInputChange = viewModel::updateInput,
            onSend = { viewModel.send() },
            onStop = { viewModel.stopGeneration() },
            onRegenerate = { viewModel.regenerateLastAnswer() }
        )
    }
}

@Composable
private fun ChatHeader(
    model: String,
    apiKeyConfigured: Boolean,
    messageCount: Int,
    onClear: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Chat",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "A focused workspace for Android builds, agents, prompts, UI polish, and debugging.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Text(
                text = "Clear",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onClear() }
            )
        }

        Spacer(Modifier.height(14.dp))

        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Assistant Status",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = if (apiKeyConfigured) {
                            "OpenRouter connected and ready"
                        } else {
                            "OpenRouter key required in Settings"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(
                    text = if (apiKeyConfigured) "ONLINE" else "SETUP",
                    active = apiKeyConfigured
                )
            }

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )

            Spacer(Modifier.height(12.dp))

            MetadataRow(
                label = "Model",
                value = model
            )

            MetadataRow(
                label = "User messages",
                value = messageCount.toString()
            )
        }
    }
}

@Composable
private fun PromptSuggestionRow(
    onSuggestionClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Title("Prompt Starters")
            Subtitle("Tap one")
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PromptSuggestions.forEach { prompt ->
                AssistChip(
                    onClick = { onSuggestionClick(prompt) },
                    label = {
                        Text(prompt)
                    }
                )
            }
        }
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
        Surface(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.88f else 0.96f),
            color = if (isUser) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
            },
            border = BorderStroke(
                width = 1.dp,
                color = if (isUser) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
                } else {
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.20f)
                }
            ),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = if (isUser) 24.dp else 6.dp,
                bottomEnd = if (isUser) 6.dp else 24.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUser) "You" else "Mr. Robot",
                        color = if (isUser) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Copy",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            clipboard.setText(
                                AnnotatedString(message.content)
                            )
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))

                MessageText(
                    content = message.content,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun MessageText(
    content: String,
    textColor: Color
) {
    val isCodeLike =
        content.contains("```") ||
            content.lines().any {
                val line = it.trim()
                line.startsWith("fun ") ||
                    line.startsWith("class ") ||
                    line.startsWith("val ") ||
                    line.startsWith("var ") ||
                    line.startsWith("import ") ||
                    line.startsWith("package ") ||
                    line.startsWith("plugins ") ||
                    line.startsWith("implementation(") ||
                    line.startsWith("android {") ||
                    line.startsWith("name:")
            }

    Text(
        text = content.replace("```", ""),
        color = textColor,
        fontFamily = if (isCodeLike) FontFamily.Monospace else FontFamily.Default,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
}

@Composable
private fun ThinkingBubble() {
    Surface(
        modifier = Modifier.fillMaxWidth(0.96f),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
        ),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 6.dp,
            bottomEnd = 24.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = "Mr. Robot is thinking...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
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
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Request failed",
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(Modifier.height(10.dp))

            OutlinedButton(onClick = onRetry) {
                Text("Retry last prompt")
            }
        }
    }
}

@Composable
private fun PromptInputPanel(
    input: String,
    isLoading: Boolean,
    canRegenerate: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onRegenerate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = {
                    Text("Ask Mr. Robot about your app...")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 1,
                maxLines = 5,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (isLoading) {
                            onStop()
                        } else {
                            onSend()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoading) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        contentColor = if (isLoading) {
                            MaterialTheme.colorScheme.onTertiary
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
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
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Regenerate")
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun StatusBadge(
    text: String,
    active: Boolean
) {
    Surface(
        color = if (active) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colorScheme.error.copy(alpha = 0.10f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (active) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.40f)
            } else {
                MaterialTheme.colorScheme.error.copy(alpha = 0.30f)
            }
        ),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = if (active) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}
