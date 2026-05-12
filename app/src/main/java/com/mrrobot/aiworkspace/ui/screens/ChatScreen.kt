package com.mrrobot.aiworkspace.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.NeonCyan
import com.mrrobot.aiworkspace.ui.components.NeonPurple
import com.mrrobot.aiworkspace.ui.components.Panel
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.SoftText
import com.mrrobot.aiworkspace.ui.components.Subtitle
import com.mrrobot.aiworkspace.ui.components.Title
import com.mrrobot.aiworkspace.viewmodel.ChatAttachment
import com.mrrobot.aiworkspace.viewmodel.ChatUiMessage
import com.mrrobot.aiworkspace.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val attachmentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val attachments = uris.map { uri ->
            createAttachmentFromUri(
                context = context,
                uri = uri
            )
        }

        viewModel.addAttachments(attachments)
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
                Subtitle("Provider-aware workspace chat with files, prompts, and Android development context.")
            }

            TextButton(onClick = { viewModel.clearChat() }) {
                Text("Clear")
            }
        }

        Spacer(Modifier.height(10.dp))

        AssistantStatusCard(
            provider = state.provider,
            model = state.model,
            ready = state.isProviderReady,
            userMessages = state.userMessages,
            queuedFiles = state.queuedFiles,
            readableFiles = state.readableFiles,
            visionImages = state.visionImages
        )

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

            Spacer(Modifier.height(10.dp))
        }

        PromptInputBar(
            input = state.input,
            isLoading = state.isLoading,
            selectedAttachments = state.selectedAttachments,
            onInputChange = viewModel::updateInput,
            onPickFiles = {
                attachmentLauncher.launch(
                    arrayOf(
                        "text/*",
                        "image/*",
                        "application/pdf",
                        "application/json",
                        "application/xml",
                        "application/zip",
                        "*/*"
                    )
                )
            },
            onRemoveAttachment = { attachment ->
                viewModel.removeAttachment(attachment)
            },
            onSend = { viewModel.send() },
            onStop = { viewModel.stopGeneration() },
            onRegenerate = { viewModel.regenerateLastAnswer() },
            canRegenerate = state.messages.any { it.role == "user" }
        )
    }
}

@Composable
private fun AssistantStatusCard(
    provider: String,
    model: String,
    ready: Boolean,
    userMessages: Int,
    queuedFiles: Int,
    readableFiles: Int,
    visionImages: Int
) {
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

                Subtitle(
                    if (ready) {
                        "$provider ready"
                    } else {
                        "No active model configured"
                    }
                )
            }

            Surface(
                color = if (ready) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                } else {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                },
                border = BorderStroke(
                    1.dp,
                    if (ready) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = if (ready) "READY" else "SETUP",
                    color = if (ready) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        StatusLine("Model", model)
        StatusLine("User messages", userMessages.toString())
        StatusLine("Queued files", queuedFiles.toString())
        StatusLine("Readable files", readableFiles.toString())
        StatusLine("Vision images", visionImages.toString())
    }
}

@Composable
private fun StatusLine(
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
        Subtitle(label)

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
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
                .fillMaxWidth(if (isUser) 0.90f else 0.96f)
                .background(
                    color = if (isUser) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    },
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
                    text = if (isUser) "You" else "ALPHA",
                    color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Copy",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        clipboard.setText(AnnotatedString(message.content))
                    }
                )
            }

            if (message.attachments.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))

                message.attachments.forEach { attachment ->
                    AttachmentChip(
                        attachment = attachment,
                        onRemove = null
                    )
                }
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
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace
        )
    } else {
        Text(
            text = content,
            color = MaterialTheme.colorScheme.onSurface
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

            Subtitle("ALPHA is thinking...")
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
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Request failed",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(10.dp))

            OutlinedButton(onClick = onRetry) {
                Text("Retry last prompt")
            }
        }
    }
}

@Composable
private fun PromptInputBar(
    input: String,
    isLoading: Boolean,
    selectedAttachments: List<ChatAttachment>,
    canRegenerate: Boolean,
    onInputChange: (String) -> Unit,
    onPickFiles: () -> Unit,
    onRemoveAttachment: (ChatAttachment) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onRegenerate: () -> Unit
) {
    GlassCard {
        if (selectedAttachments.isNotEmpty()) {
            selectedAttachments.forEach { attachment ->
                AttachmentChip(
                    attachment = attachment,
                    onRemove = {
                        onRemoveAttachment(attachment)
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPickFiles,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Text(
                    text = "+",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Text(
                    text = "🎙",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = { Text("Ask ALPHA...") },
                modifier = Modifier.weight(1f),
                minLines = 1,
                maxLines = 5
            )
        }

        Spacer(Modifier.height(10.dp))

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
                    containerColor = if (isLoading) Color(0xFFFFB020) else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
}

@Composable
private fun AttachmentChip(
    attachment: ChatAttachment,
    onRemove: (() -> Unit)?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attachment.displayName.ifBlank { attachment.name },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = listOfNotNull(
                        attachment.mimeType?.takeIf { it.isNotBlank() },
                        attachment.sizeLabel.takeIf { it.isNotBlank() },
                        attachment.extractionStatus.takeIf { it.isNotBlank() }
                    ).joinToString(" • ").ifBlank { "Attached file" },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (onRemove != null) {
                Text(
                    text = "Remove",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onRemove() }
                        .padding(start = 10.dp)
                )
            }
        }
    }
}

private fun createAttachmentFromUri(
    context: Context,
    uri: Uri
): ChatAttachment {
    val resolver = context.contentResolver
    val mimeType = resolver.getType(uri).orEmpty()
    var name = uri.lastPathSegment ?: "Attachment"
    var sizeBytes = 0L

    resolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

        if (cursor.moveToFirst()) {
            if (nameIndex >= 0) {
                name = cursor.getString(nameIndex) ?: name
            }

            if (sizeIndex >= 0) {
                sizeBytes = cursor.getLong(sizeIndex)
            }
        }
    }

    val extractedText = if (
        mimeType.startsWith("text/") ||
        name.endsWith(".kt", true) ||
        name.endsWith(".java", true) ||
        name.endsWith(".xml", true) ||
        name.endsWith(".json", true) ||
        name.endsWith(".gradle", true) ||
        name.endsWith(".md", true) ||
        name.endsWith(".txt", true)
    ) {
        runCatching {
            resolver.openInputStream(uri)?.use { input ->
                BufferedReader(InputStreamReader(input)).use { reader ->
                    reader.readText().take(12000)
                }
            }
        }.getOrNull()
    } else {
        null
    }

    val status = when {
        mimeType.startsWith("image/") -> "Image ready"
        !extractedText.isNullOrBlank() -> "Text extracted"
        else -> "Queued"
    }

    return ChatAttachment(
        uri = uri,
        name = name,
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        sizeLabel = formatSize(sizeBytes),
        extractedText = extractedText,
        extractionStatus = status
    )
}

private fun formatSize(bytes: Long): String {
    if (bytes <= 0L) return ""

    val kb = bytes / 1024.0
    val mb = kb / 1024.0

    return if (mb >= 1.0) {
        String.format("%.1f MB", mb)
    } else {
        String.format("%.1f KB", kb)
    }
}
