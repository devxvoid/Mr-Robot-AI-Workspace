package com.mrrobot.aiworkspace.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.ui.components.GlassCard
import com.mrrobot.aiworkspace.ui.components.ScreenShell
import com.mrrobot.aiworkspace.ui.components.Subtitle
import com.mrrobot.aiworkspace.ui.components.Title
import com.mrrobot.aiworkspace.viewmodel.ChatAttachment
import com.mrrobot.aiworkspace.viewmodel.ChatUiMessage
import com.mrrobot.aiworkspace.viewmodel.ChatViewModel
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

private const val MAX_TEXT_PER_FILE = 12_000
private const val MAX_TOTAL_TEXT = 26_000

private val PromptSuggestions = listOf(
    "Summarize this file",
    "What is inside this file?",
    "Find problems in this code",
    "Explain this image",
    "Extract important points"
)

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
        uris.forEach { uri ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }

        val attachments = uris.map { uri ->
            buildChatAttachment(
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
                    attachmentCount = state.selectedAttachments.size,
                    readableCount = state.selectedAttachments.count { it.extractedText.isNotBlank() },
                    imageCount = state.selectedAttachments.count { it.imageDataUrl != null },
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
            selectedAttachments = state.selectedAttachments,
            isLoading = state.isLoading,
            canRegenerate = state.messages.any { it.role == "user" },
            onInputChange = viewModel::updateInput,
            onAddAttachment = {
                attachmentLauncher.launch(
                    arrayOf(
                        "image/*",
                        "application/pdf",
                        "text/*",
                        "application/json",
                        "application/xml",
                        "application/zip",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "*/*"
                    )
                )
            },
            onRemoveAttachment = viewModel::removeAttachment,
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
    attachmentCount: Int,
    readableCount: Int,
    imageCount: Int,
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
                    text = "Attach files, read content, analyze screenshots, and coordinate Android development work.",
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
                            "OpenRouter connected. Text, PDF, DOCX, ZIP index, and image vision inputs are supported."
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

            MetadataRow("Model", model)
            MetadataRow("User messages", messageCount.toString())
            MetadataRow("Queued files", attachmentCount.toString())
            MetadataRow("Readable files", readableCount.toString())
            MetadataRow("Vision images", imageCount.toString())
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

                if (message.attachments.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))

                    AttachmentTray(
                        attachments = message.attachments,
                        removable = false,
                        onRemove = {}
                    )
                }
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
                text = "Mr. Robot is analyzing...",
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
    selectedAttachments: List<ChatAttachment>,
    isLoading: Boolean,
    canRegenerate: Boolean,
    onInputChange: (String) -> Unit,
    onAddAttachment: () -> Unit,
    onRemoveAttachment: (Long) -> Unit,
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
            if (selectedAttachments.isNotEmpty()) {
                AttachmentTray(
                    attachments = selectedAttachments,
                    removable = true,
                    onRemove = onRemoveAttachment
                )

                Spacer(Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onAddAttachment() },
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedTextField(
                    value = input,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text("Ask Mr. Robot or attach files...")
                    },
                    modifier = Modifier.weight(1f),
                    minLines = 1,
                    maxLines = 5,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

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
private fun AttachmentTray(
    attachments: List<ChatAttachment>,
    removable: Boolean,
    onRemove: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        attachments.forEach { attachment ->
            AttachmentPill(
                attachment = attachment,
                removable = removable,
                onRemove = onRemove
            )
        }
    }
}

@Composable
private fun AttachmentPill(
    attachment: ChatAttachment,
    removable: Boolean,
    onRemove: (Long) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
        ),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier
                .width(235.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = attachmentIcon(attachment),
                fontSize = 14.sp
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = attachment.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = attachmentStatusLabel(attachment),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (removable) {
                Text(
                    text = "×",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onRemove(attachment.id)
                    }
                )
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
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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

private fun buildChatAttachment(
    context: Context,
    uri: Uri
): ChatAttachment {
    var displayName = uri.lastPathSegment ?: "attachment"
    var sizeBytes: Long? = null

    context.contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

            if (nameIndex >= 0) {
                displayName = cursor.getString(nameIndex) ?: displayName
            }

            if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                sizeBytes = cursor.getLong(sizeIndex)
            }
        }
    }

    val mimeType = context.contentResolver.getType(uri)
        ?: guessMimeType(displayName)

    val imageDataUrl = if (mimeType.startsWith("image/")) {
        buildImageDataUrl(context, uri)
    } else {
        null
    }

    val extracted = extractAttachmentText(
        context = context,
        uri = uri,
        displayName = displayName,
        mimeType = mimeType
    )

    return ChatAttachment(
        uri = uri.toString(),
        name = displayName,
        mimeType = mimeType,
        sizeLabel = formatFileSize(sizeBytes),
        imageDataUrl = imageDataUrl,
        extractedText = extracted.text,
        extractionStatus = extracted.status
    )
}

private data class ExtractionResult(
    val text: String,
    val status: String
)

private fun extractAttachmentText(
    context: Context,
    uri: Uri,
    displayName: String,
    mimeType: String
): ExtractionResult {
    return when {
        mimeType.startsWith("image/") -> {
            ExtractionResult(
                text = "",
                status = if (buildImageDataUrl(context, uri) != null) {
                    "vision image ready"
                } else {
                    "image metadata only"
                }
            )
        }

        isPlainTextLike(displayName, mimeType) -> {
            val text = readPlainText(context, uri)
            ExtractionResult(
                text = text,
                status = if (text.isBlank()) "empty text file" else "text extracted"
            )
        }

        mimeType == "application/pdf" || displayName.endsWith(".pdf", ignoreCase = true) -> {
            val text = readPdfText(context, uri)
            ExtractionResult(
                text = text,
                status = if (text.isBlank()) "PDF has no extractable text" else "PDF text extracted"
            )
        }

        mimeType.contains("wordprocessingml") || displayName.endsWith(".docx", ignoreCase = true) -> {
            val text = readDocxText(context, uri)
            ExtractionResult(
                text = text,
                status = if (text.isBlank()) "DOCX has no extractable text" else "DOCX text extracted"
            )
        }

        mimeType.contains("zip") || displayName.endsWith(".zip", ignoreCase = true) -> {
            val text = readZipIndex(context, uri)
            ExtractionResult(
                text = text,
                status = if (text.isBlank()) "ZIP index unavailable" else "ZIP file list extracted"
            )
        }

        else -> {
            ExtractionResult(
                text = "",
                status = "unsupported binary file"
            )
        }
    }
}

private fun readPlainText(
    context: Context,
    uri: Uri
): String {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
            reader.readText()
        }.orEmpty().take(MAX_TEXT_PER_FILE)
    }.getOrDefault("")
}

private fun readPdfText(
    context: Context,
    uri: Uri
): String {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            PDDocument.load(input).use { document ->
                PDFTextStripper()
                    .getText(document)
                    .take(MAX_TEXT_PER_FILE)
            }
        }.orEmpty()
    }.getOrDefault("")
}

private fun readDocxText(
    context: Context,
    uri: Uri
): String {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input).use { zip ->
                val builder = StringBuilder()

                while (true) {
                    val entry = zip.nextEntry ?: break

                    if (entry.name == "word/document.xml") {
                        val xml = zip.bufferedReader().readText()

                        val text = xml
                            .replace(Regex("<w:p[^>]*>"), "\n")
                            .replace(Regex("<[^>]+>"), " ")
                            .replace("&amp;", "&")
                            .replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("&quot;", "\"")
                            .replace("&apos;", "'")
                            .replace(Regex("\\s+"), " ")
                            .trim()

                        builder.append(text)
                        break
                    }
                }

                builder.toString().take(MAX_TEXT_PER_FILE)
            }
        }.orEmpty()
    }.getOrDefault("")
}

private fun readZipIndex(
    context: Context,
    uri: Uri
): String {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input).use { zip ->
                buildString {
                    appendLine("ZIP contents:")

                    var count = 0

                    while (true) {
                        val entry = zip.nextEntry ?: break
                        count++

                        append("- ")
                        append(entry.name)

                        if (entry.size > 0) {
                            append(" (")
                            append(formatFileSize(entry.size))
                            append(")")
                        }

                        appendLine()

                        if (count >= 300) {
                            appendLine("...truncated after 300 entries")
                            break
                        }
                    }
                }.take(MAX_TEXT_PER_FILE)
            }
        }.orEmpty()
    }.getOrDefault("")
}

private fun buildImageDataUrl(
    context: Context,
    uri: Uri
): String? {
    return runCatching {
        val bitmap = context.contentResolver.openInputStream(uri).use { input ->
            BitmapFactory.decodeStream(input)
        } ?: return null

        val scaledBitmap = scaleBitmapForVision(bitmap)

        val output = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 82, output)

        val base64 = Base64.encodeToString(
            output.toByteArray(),
            Base64.NO_WRAP
        )

        "data:image/jpeg;base64,$base64"
    }.getOrNull()
}

private fun scaleBitmapForVision(bitmap: Bitmap): Bitmap {
    val maxDimension = 1280
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxDimension && height <= maxDimension) {
        return bitmap
    }

    val scale = minOf(
        maxDimension.toFloat() / width.toFloat(),
        maxDimension.toFloat() / height.toFloat()
    )

    val newWidth = (width * scale).toInt().coerceAtLeast(1)
    val newHeight = (height * scale).toInt().coerceAtLeast(1)

    return Bitmap.createScaledBitmap(
        bitmap,
        newWidth,
        newHeight,
        true
    )
}

private fun isPlainTextLike(
    name: String,
    mimeType: String
): Boolean {
    val lower = name.lowercase()

    return mimeType.startsWith("text/") ||
        mimeType.contains("json") ||
        mimeType.contains("xml") ||
        lower.endsWith(".txt") ||
        lower.endsWith(".md") ||
        lower.endsWith(".json") ||
        lower.endsWith(".xml") ||
        lower.endsWith(".yaml") ||
        lower.endsWith(".yml") ||
        lower.endsWith(".csv") ||
        lower.endsWith(".log") ||
        lower.endsWith(".kt") ||
        lower.endsWith(".kts") ||
        lower.endsWith(".java") ||
        lower.endsWith(".gradle") ||
        lower.endsWith(".py") ||
        lower.endsWith(".js") ||
        lower.endsWith(".ts") ||
        lower.endsWith(".html") ||
        lower.endsWith(".css") ||
        lower.endsWith(".sh")
}

private fun guessMimeType(name: String): String {
    val lower = name.lowercase()

    return when {
        lower.endsWith(".pdf") -> "application/pdf"
        lower.endsWith(".docx") -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        lower.endsWith(".zip") -> "application/zip"
        lower.endsWith(".json") -> "application/json"
        lower.endsWith(".xml") -> "application/xml"
        lower.endsWith(".txt") -> "text/plain"
        lower.endsWith(".md") -> "text/markdown"
        lower.endsWith(".kt") -> "text/x-kotlin"
        lower.endsWith(".java") -> "text/x-java"
        lower.endsWith(".py") -> "text/x-python"
        lower.endsWith(".js") -> "text/javascript"
        lower.endsWith(".ts") -> "text/typescript"
        lower.endsWith(".html") -> "text/html"
        lower.endsWith(".css") -> "text/css"
        lower.endsWith(".png") -> "image/png"
        lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
        lower.endsWith(".webp") -> "image/webp"
        else -> "application/octet-stream"
    }
}

private fun formatFileSize(sizeBytes: Long?): String {
    if (sizeBytes == null || sizeBytes <= 0L) return ""

    val kb = sizeBytes / 1024.0
    val mb = kb / 1024.0

    return if (mb >= 1.0) {
        String.format("%.1f MB", mb)
    } else {
        String.format("%.1f KB", kb)
    }
}

private fun attachmentIcon(attachment: ChatAttachment): String {
    return when {
        attachment.imageDataUrl != null -> "IMG"
        attachment.mimeType.startsWith("image/") -> "IMG"
        attachment.mimeType == "application/pdf" -> "PDF"
        attachment.mimeType.contains("wordprocessingml") -> "DOCX"
        attachment.mimeType.contains("zip") -> "ZIP"
        attachment.mimeType.startsWith("text/") -> "TXT"
        attachment.mimeType.contains("json") -> "JSON"
        attachment.mimeType.contains("xml") -> "XML"
        else -> "FILE"
    }
}

private fun attachmentStatusLabel(attachment: ChatAttachment): String {
    val base = when {
        attachment.imageDataUrl != null -> "Vision ready"
        attachment.extractedText.isNotBlank() -> attachment.extractionStatus
        else -> attachment.extractionStatus.ifBlank { attachment.mimeType }
    }

    return if (attachment.sizeLabel.isNotBlank()) {
        "$base • ${attachment.sizeLabel}"
    } else {
        base
    }
}
