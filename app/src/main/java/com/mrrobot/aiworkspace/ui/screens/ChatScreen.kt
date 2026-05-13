package com.mrrobot.aiworkspace.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.R
import com.mrrobot.aiworkspace.navigation.mergedScreenPadding
import com.mrrobot.aiworkspace.ui.components.GroupSpacing
import com.mrrobot.aiworkspace.viewmodel.ChatAttachment
import com.mrrobot.aiworkspace.viewmodel.ChatUiMessage
import com.mrrobot.aiworkspace.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

private val SuggestionPrompts = listOf(
    "Review my latest Android build errors and suggest fixes",
    "Refactor this Kotlin file for readability",
    "Write a Compose screen from a UI description",
    "Summarize the attached document"
)

/**
 * Chat screen.
 *
 * Layout follows the canonical M3 conversational surface:
 *
 *   - Root [Scaffold] with a pinned [TopAppBar] on top.
 *   - Scrollable [LazyColumn] of chat bubbles.
 *   - Bottom composer using [OutlinedTextField] (rounded via
 *     MaterialTheme.shapes.extraLarge for the pill look) plus a
 *     [FloatingActionButton] anchored to the send action.
 *   - "+" attach button opens a [ModalBottomSheet] (M3's
 *     ModalBottomSheet component, not a custom overlay) with Gallery /
 *     Videos / Files / Camera options wired to ActivityResult
 *     contracts (PickVisualMedia is available starting at Android 13
 *     equivalent via ActivityResultContracts; the app uses
 *     GetMultipleContents + OpenMultipleDocuments as a universal
 *     fallback and TakePicture for the camera).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    parentPadding: PaddingValues = PaddingValues()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior(rememberTopAppBarState())

    var showAttachSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addAttachments(uris.map { uriToAttachment(context, it) })
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addAttachments(uris.map { uriToAttachment(context, it) })
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = cameraImageUri
        if (success && uri != null) {
            val attachment = uriToAttachment(context, uri).copy(
                name = "Camera Photo",
                displayName = "Camera Photo"
            )
            viewModel.addAttachments(listOf(attachment))
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createCameraImageUri(context)
            cameraImageUri = uri
            if (uri != null) {
                cameraLauncher.launch(uri)
            }
        } else {
            Toast.makeText(
                context,
                "Camera permission is required to take photos.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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

    if (showAttachSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = MaterialTheme.shapes.large
        ) {
            AttachmentSheetContent(
                onGalleryClick = {
                    showAttachSheet = false
                    galleryLauncher.launch("image/*")
                },
                onVideoClick = {
                    showAttachSheet = false
                    galleryLauncher.launch("video/*")
                },
                onFilesClick = {
                    showAttachSheet = false
                    fileLauncher.launch(arrayOf("*/*"))
                },
                onCameraClick = {
                    showAttachSheet = false
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mr. Robot",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (state.isProviderReady) {
                                "${state.provider} - ${state.assistantStatus}"
                            } else {
                                state.assistantStatus
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lucide_sparkles),
                            contentDescription = "Clear chat"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        val padding = mergedScreenPadding(
            innerPadding = innerPadding,
            parentPadding = parentPadding,
            horizontalEdge = 0.dp,
            topExtra = 0.dp,
            bottomExtra = 0.dp
        )

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            StatusStrip(
                model = state.model,
                isReady = state.isProviderReady,
                userMessages = state.userMessages,
                queuedFiles = state.queuedFiles,
                readableFiles = state.readableFiles,
                visionImages = state.visionImages
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.messages.isEmpty()) {
                    item {
                        EmptyChatState(
                            isReady = state.isProviderReady,
                            onPrompt = viewModel::useSuggestion
                        )
                    }
                } else {
                    items(state.messages, key = { it.id }) { message ->
                        ChatBubble(message = message)
                    }
                }

                if (state.isLoading) {
                    item { ThinkingBubble() }
                }
            }

            AnimatedVisibility(
                visible = state.error.isNotBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    ErrorPanel(
                        message = state.error,
                        onRetry = { viewModel.retryLast() }
                    )
                }
            }

            SuggestionRow(
                visible = state.input.isBlank() && !state.isLoading && state.messages.size <= 1,
                onPrompt = viewModel::useSuggestion
            )

            AttachmentPreviewStrip(
                attachments = state.selectedAttachments,
                onRemove = { viewModel.removeAttachment(it) }
            )

            PromptComposer(
                input = state.input,
                isLoading = state.isLoading,
                canRegenerate = state.messages.any { it.role == "user" },
                queuedFiles = state.queuedFiles,
                onInputChange = viewModel::updateInput,
                onMicClick = { launchSpeechInput(context, speechLauncher::launch) },
                onAttachClick = { showAttachSheet = true },
                onSend = { viewModel.send() },
                onStop = { viewModel.stopGeneration() },
                onRegenerate = { viewModel.regenerateLastAnswer() }
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Attachment bottom sheet (M3 ModalBottomSheet)                      */
/* ------------------------------------------------------------------ */

@Composable
private fun AttachmentSheetContent(
    onGalleryClick: () -> Unit,
    onVideoClick: () -> Unit,
    onFilesClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Attach to message",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Send photos, videos, files, or take a new picture.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AttachmentOption(
                iconRes = R.drawable.ic_lucide_image,
                label = "Gallery",
                onClick = onGalleryClick
            )

            AttachmentOption(
                iconRes = R.drawable.ic_lucide_image,
                label = "Videos",
                onClick = onVideoClick
            )

            AttachmentOption(
                iconRes = R.drawable.ic_lucide_file,
                label = "Files",
                onClick = onFilesClick
            )

            AttachmentOption(
                iconRes = R.drawable.ic_lucide_camera,
                label = "Camera",
                onClick = onCameraClick
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AttachmentOption(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/* ------------------------------------------------------------------ */
/* Attachment preview strip                                           */
/* ------------------------------------------------------------------ */

@Composable
private fun AttachmentPreviewStrip(
    attachments: List<ChatAttachment>,
    onRemove: (ChatAttachment) -> Unit
) {
    if (attachments.isEmpty()) return

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(attachments, key = { it.stableKey }) { attachment ->
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(start = 10.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (attachment.isImage) R.drawable.ic_lucide_image
                            else R.drawable.ic_lucide_file
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = attachment.displayName.take(20),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = { onRemove(attachment) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lucide_x),
                            contentDescription = "Remove",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Utility: URI to Attachment, camera URI                             */
/* ------------------------------------------------------------------ */

private fun uriToAttachment(context: Context, uri: Uri): ChatAttachment {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri) ?: ""
    var fileName = "Attachment"
    var fileSize = 0L

    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (nameIndex >= 0) fileName = cursor.getString(nameIndex) ?: "Attachment"
            if (sizeIndex >= 0) fileSize = cursor.getLong(sizeIndex)
        }
    }

    val sizeLabel = when {
        fileSize < 1024 -> "$fileSize B"
        fileSize < 1024 * 1024 -> "${fileSize / 1024} KB"
        else -> String.format(Locale.US, "%.1f MB", fileSize / (1024.0 * 1024.0))
    }

    return ChatAttachment(
        uri = uri,
        name = fileName,
        mimeType = mimeType,
        sizeBytes = fileSize,
        sizeLabel = sizeLabel,
        extractionStatus = "Queued"
    )
}

private fun createCameraImageUri(context: Context): Uri? {
    return try {
        val cacheDir = File(context.cacheDir, "camera_photos")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val imageFile = File(cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (_: Exception) {
        null
    }
}

/* ------------------------------------------------------------------ */
/* Status strip                                                       */
/* ------------------------------------------------------------------ */

@Composable
private fun StatusStrip(
    model: String,
    isReady: Boolean,
    userMessages: Int,
    queuedFiles: Int,
    readableFiles: Int,
    visionImages: Int
) {
    val scroll = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoChip(
            iconRes = R.drawable.ic_lucide_cpu,
            label = if (isReady) model else "No model",
            emphasized = true
        )

        InfoChip(
            iconRes = R.drawable.ic_lucide_user,
            label = "$userMessages sent"
        )

        if (queuedFiles > 0) {
            InfoChip(
                iconRes = R.drawable.ic_lucide_folder,
                label = "$queuedFiles queued"
            )
        }

        if (readableFiles > 0) {
            InfoChip(
                iconRes = R.drawable.ic_lucide_terminal,
                label = "$readableFiles readable"
            )
        }

        if (visionImages > 0) {
            InfoChip(
                iconRes = R.drawable.ic_lucide_sparkles,
                label = "$visionImages vision"
            )
        }
    }
}

@Composable
private fun InfoChip(
    iconRes: Int,
    label: String,
    emphasized: Boolean = false
) {
    val container = if (emphasized) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val content = if (emphasized) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        color = container,
        contentColor = content,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Empty state                                                        */
/* ------------------------------------------------------------------ */

@Composable
private fun EmptyChatState(
    isReady: Boolean,
    onPrompt: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        AssistantAvatar(size = 72.dp)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "How can I help you today?",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = if (isReady) {
                "Ask anything, attach files or screenshots, or dictate your prompt."
            } else {
                "Add an API key in Settings, then start a conversation."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(GroupSpacing)
        ) {
            SuggestionPrompts.forEach { prompt ->
                SuggestionCard(
                    text = prompt,
                    onClick = { onPrompt(prompt) }
                )
            }
        }
    }
}

@Composable
private fun SuggestionCard(
    text: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lucide_sparkles),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Chat bubble                                                        */
/* ------------------------------------------------------------------ */

@Composable
private fun ChatBubble(message: ChatUiMessage) {
    val clipboard = LocalClipboardManager.current
    val scheme = MaterialTheme.colorScheme
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            AssistantAvatar(size = 34.dp)
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 340.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Text(
                text = if (isUser) "You" else "Mr. Robot",
                style = MaterialTheme.typography.labelSmall,
                color = scheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            val container = if (isUser) scheme.primary else scheme.surfaceContainerHigh
            val content = if (isUser) scheme.onPrimary else scheme.onSurface

            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                color = container,
                contentColor = content,
                tonalElevation = if (isUser) 0.dp else 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    MessageContent(content = message.content, isUser = isUser)
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (message.attachments.isNotEmpty()) {
                    Text(
                        text = "${message.attachments.size} attachment" +
                            if (message.attachments.size == 1) "" else "s",
                        style = MaterialTheme.typography.labelSmall,
                        color = scheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "Copy",
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.primary,
                    modifier = Modifier.clickable {
                        clipboard.setText(AnnotatedString(message.content))
                    }
                )
            }
        }

        if (isUser) {
            Spacer(Modifier.width(8.dp))
            UserAvatar(size = 34.dp)
        }
    }
}

@Composable
private fun MessageContent(content: String, isUser: Boolean) {
    val looksLikeCode = content.contains("```") ||
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
        val scheme = MaterialTheme.colorScheme
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isUser) scheme.primary.copy(alpha = 0.3f) else scheme.surfaceContainerHighest,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = content.replace("```", ""),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.padding(12.dp)
            )
        }
    } else {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/* ------------------------------------------------------------------ */
/* Avatars                                                            */
/* ------------------------------------------------------------------ */

@Composable
private fun AssistantAvatar(size: Dp) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lucide_bot),
                contentDescription = null,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}

@Composable
private fun UserAvatar(size: Dp) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lucide_user),
                contentDescription = null,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Thinking indicator                                                 */
/* ------------------------------------------------------------------ */

@Composable
private fun ThinkingBubble() {
    val scheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistantAvatar(size = 34.dp)
        Spacer(Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 4.dp,
                bottomEnd = 18.dp
            ),
            color = scheme.surfaceContainerHigh,
            contentColor = scheme.onSurface,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = scheme.primary
                )

                Text(
                    text = "Thinking",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Error panel                                                        */
/* ------------------------------------------------------------------ */

@Composable
private fun ErrorPanel(
    message: String,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Request failed",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(10.dp))

            Surface(
                modifier = Modifier.clickable { onRetry() },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ) {
                Text(
                    text = "Retry",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Suggestion row                                                     */
/* ------------------------------------------------------------------ */

@Composable
private fun SuggestionRow(
    visible: Boolean,
    onPrompt: (String) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val scroll = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SuggestionPrompts.forEach { prompt ->
                Surface(
                    modifier = Modifier.clickable { onPrompt(prompt) },
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* Prompt composer                                                    */
/* ------------------------------------------------------------------ */

@Composable
private fun PromptComposer(
    input: String,
    isLoading: Boolean,
    canRegenerate: Boolean,
    queuedFiles: Int,
    onInputChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onAttachClick: () -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onRegenerate: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ComposerIconButton(
                iconRes = R.drawable.ic_lucide_plus,
                contentDescription = "Attach",
                badgeCount = queuedFiles,
                onClick = onAttachClick
            )

            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp, max = 160.dp)
                    .defaultMinSize(minHeight = 48.dp),
                placeholder = { Text("Message Mr. Robot...") },
                shape = MaterialTheme.shapes.extraLarge,
                maxLines = 6,
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

            ComposerIconButton(
                iconRes = R.drawable.ic_lucide_mic,
                contentDescription = "Voice input",
                onClick = onMicClick
            )

            SendFab(
                isLoading = isLoading,
                canSend = input.isNotBlank() || isLoading,
                canRegenerate = canRegenerate,
                onSend = onSend,
                onStop = onStop,
                onRegenerate = onRegenerate
            )
        }
    }
}

@Composable
private fun ComposerIconButton(
    iconRes: Int,
    contentDescription: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Box(contentAlignment = Alignment.TopEnd) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = scheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        if (badgeCount > 0) {
            Surface(
                modifier = Modifier.size(16.dp),
                shape = CircleShape,
                color = scheme.primary,
                contentColor = scheme.onPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = badgeCount.coerceAtMost(9).toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SendFab(
    isLoading: Boolean,
    canSend: Boolean,
    canRegenerate: Boolean,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onRegenerate: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val showRegenerate = !isLoading && !canSend && canRegenerate

    val container = when {
        isLoading -> scheme.errorContainer
        showRegenerate -> scheme.secondaryContainer
        canSend -> scheme.primaryContainer
        else -> scheme.surfaceContainerHighest
    }

    val content = when {
        isLoading -> scheme.onErrorContainer
        showRegenerate -> scheme.onSecondaryContainer
        canSend -> scheme.onPrimaryContainer
        else -> scheme.onSurfaceVariant
    }

    val iconRes = when {
        isLoading -> R.drawable.ic_lucide_cpu
        showRegenerate -> R.drawable.ic_lucide_sparkles
        else -> R.drawable.ic_lucide_sparkles
    }

    val description = when {
        isLoading -> "Stop generating"
        showRegenerate -> "Regenerate last response"
        else -> "Send message"
    }

    FloatingActionButton(
        onClick = {
            when {
                isLoading -> onStop()
                canSend -> onSend()
                showRegenerate -> onRegenerate()
            }
        },
        modifier = Modifier.size(48.dp),
        shape = MaterialTheme.shapes.large,
        containerColor = container,
        contentColor = content,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = content
            )
        } else {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = description,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Voice input                                                        */
/* ------------------------------------------------------------------ */

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
