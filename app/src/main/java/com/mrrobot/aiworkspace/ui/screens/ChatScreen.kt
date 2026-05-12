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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.R
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.Subtitle
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
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Chat",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 40.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp
                )

                Spacer(Modifier.height(8.dp))

                Subtitle(
                    "Attach files, speak prompts, analyze screenshots, and coordinate Android development work."
                )
            }

            TextButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "Clear",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        PremiumAssistantStatusCard(
            provider = state.provider,
            model = state.model,
            ready = state.isProviderReady,
            userMessages = state.userMessages,
            queuedFiles = state.queuedFiles,
            readableFiles = state.readableFiles,
            visionImages = state.visionImages
        )

        Spacer(Modifier.height(14.dp))

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
                PremiumChatBubble(message = message)
            }

            if (state.isLoading) {
                item {
                    ThinkingBubble()
                }
            }
        }

        if (state.error.isNotBlank()) {
            PremiumErrorPanel(
                message = state.error,
                onRetry = { viewModel.retryLast() }
            )

            Spacer(Modifier.height(10.dp))
        }

        PremiumPromptInputBar(
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
private fun PremiumAssistantStatusCard(
    provider: String,
    model: String,
    ready: Boolean,
    userMessages: Int,
    queuedFiles: Int,
    readableFiles: Int,
    visionImages: Int
) {
    PremiumSurfaceCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Assistant Status",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = if (ready) {
                        "$provider ready"
                    } else {
                        "No active model configured"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 18.sp,
                    lineHeight = 25.sp
                )
            }

            Surface(
                color = if (ready) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.11f)
                } else {
                    MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                },
                border = BorderStroke(
                    1.4.dp,
                    if (ready) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                    }
                ),
                shape = RoundedCornerShape(999.dp)
            ) {
                Text(
                    text = if (ready) "READY" else "SETUP",
                    color = if (ready) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        ThinDivider()

        Spacer(Modifier.height(14.dp))

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
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PremiumChatBubble(message: ChatUiMessage) {
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
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(
                        topStart = 26.dp,
                        topEnd = 26.dp,
                        bottomStart = if (isUser) 26.dp else 8.dp,
                        bottomEnd = if (isUser) 8.dp else 26.dp
                    ),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
                .background(
                    color = if (isUser) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    },
                    shape = RoundedCornerShape(
                        topStart = 26.dp,
                        topEnd = 26.dp,
                        bottomStart = if (isUser) 26.dp else 8.dp,
                        bottomEnd = if (isUser) 8.dp else 26.dp
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
                    color = if (isUser) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Copy",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
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

            Spacer(Modifier.height(10.dp))

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
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    } else {
        Text(
            text = content,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            lineHeight = 27.sp
        )
    }
}

@Composable
private fun ThinkingBubble() {
    PremiumSurfaceCard(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
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
private fun PremiumErrorPanel(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MaterialTheme.colorScheme.error.copy(alpha = 0.18f),
                spotColor = MaterialTheme.colorScheme.error.copy(alpha = 0.20f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error.copy(alpha = 0.30f)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Request failed",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                lineHeight = 25.sp
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRetry,
                shape = RoundedCornerShape(999.dp)
            ) {
                Text("Retry last prompt")
            }
        }
    }
}

@Composable
private fun PremiumPromptInputBar(
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
    PremiumSurfaceCard(
        outerPadding = 2.dp
    ) {
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PremiumIconButton(
                icon = R.drawable.ic_lucide_plus,
                contentDescription = "Attach files",
                onClick = onPickFiles
            )

            PremiumIconButton(
                icon = R.drawable.ic_lucide_mic,
                contentDescription = "Voice input",
                onClick = {}
            )

            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = {
                    Text(
                        text = "Ask Mr. Robot...",
                        fontSize = 18.sp
                    )
                },
                modifier = Modifier.weight(1f),
                minLines = 1,
                maxLines = 5,
                shape = RoundedCornerShape(18.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (isLoading) onStop() else onSend()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLoading) {
                        Color(0xFFFFB020)
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = if (isLoading) "Stop" else "Send",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }

            OutlinedButton(
                onClick = onRegenerate,
                enabled = canRegenerate && !isLoading,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = "Regenerate",
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun PremiumIconButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(58.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(27.dp)
        )
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

@Composable
private fun PremiumSurfaceCard(
    modifier: Modifier = Modifier,
    outerPadding: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable Column.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(outerPadding)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                content = content
            )
        }
    }
}

@Composable
private fun ThinDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
    )
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
