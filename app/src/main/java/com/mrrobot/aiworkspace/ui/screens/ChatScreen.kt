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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.R
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme

    var showAttachSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Camera capture URI holder
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Gallery picker (photos & videos)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val attachments = uris.map { uri ->
                uriToAttachment(context, uri)
            }
            viewModel.addAttachments(attachments)
        }
    }

    // File picker (any file type)
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val attachments = uris.map { uri ->
                uriToAttachment(context, uri)
            }
            viewModel.addAttachments(attachments)
        }
    }

    // Camera capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            val attachment = uriToAttachment(context, cameraImageUri!!)
            viewModel.addAttachments(listOf(attachment.copy(name = "Camera Photo", displayName = "Camera Photo")))
        }
    }

    // Camera permission
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
            Toast.makeText(context, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
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

    // Attachment bottom sheet
    if (showAttachSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachSheet = false },
            sheetState = sheetState,
            containerColor = scheme.surface,
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        scheme.background,
                        scheme.surface.copy(alpha = 0.98f),
                        scheme.surfaceVariant.copy(alpha = 0.82f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            ChatTopBar(
                provider = state.provider,
                assistantStatus = state.assistantStatus,
                isReady = state.isProviderReady,
                onClear = { viewModel.clearChat() }
            )

            StatusStrip(
                model = state.model,
                provider = state.provider,
                isReady = state.isProviderReady,
                userMessages = state.userMessages,
                queuedFiles = state.queuedFiles,
                readableFiles = state.readableFiles,
                visionImages = state.visionImages
            )

            Spacer(Modifier.height(6.dp))

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (state.messages.isEmpty()) {
                    item {
                        EmptyChatState(
                            isReady = state.isProviderReady,
                            onPrompt = { viewModel.useSuggestion(it) }
                        )
                    }
                } else {
                    items(
                        items = state.messages,
                        key = { it.id }
                    ) { message ->
                        ChatBubble(message = message)
                    }
                }

                if (state.isLoading) {
                    item {
                        ThinkingBubble()
                    }
                }
            }

            AnimatedVisibility(
                visible = state.error.isNotBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    ErrorPanel(
                        message = state.error,
                        onRetry = { viewModel.retryLast() }
                    )
                }
            }

            SuggestionRow(
                visible = state.input.isBlank() && !state.isLoading && state.messages.size <= 1,
                onPrompt = { viewModel.useSuggestion(it) }
            )

            // Attachment preview strip
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
                onMicClick = {
                    launchSpeechInput(
                        context = context,
                        launcher = speechLauncher::launch
                    )
                },
                onAttachClick = {
                    showAttachSheet = true
                },
                onSend = { viewModel.send() },
                onStop = { viewModel.stopGeneration() },
                onRegenerate = { viewModel.regenerateLastAnswer() }
            )
        }
    }
}

/* ---------------- Attachment Bottom Sheet Content ---------------- */

@Composable
private fun AttachmentSheetContent(
    onGalleryClick: () -> Unit,
    onVideoClick: () -> Unit,
    onFilesClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = "Attach to message",
            color = scheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AttachmentOption(
                iconRes = R.drawable.ic_lucide_image,
                label = "Gallery",
                tint = scheme.primary,
                onClick = onGalleryClick
            )

            AttachmentOption(
                iconRes = R.drawable.ic_lucide_image,
                label = "Videos",
                tint = scheme.secondary,
                onClick = onVideoClick
            )

            AttachmentOption(
                iconRes = R.drawable.ic_lucide_file,
                label = "Files",
                tint = scheme.tertiary,
                onClick = onFilesClick
            )

            AttachmentOption(
                iconRes = R.drawable.ic_lucide_camera,
                label = "Camera",
                tint = scheme.error,
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
    tint: Color,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = tint.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, tint.copy(alpha = 0.3f))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = label,
            color = scheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/* ---------------- Attachment Preview Strip ---------------- */

@Composable
private fun AttachmentPreviewStrip(
    attachments: List<ChatAttachment>,
    onRemove: (ChatAttachment) -> Unit
) {
    if (attachments.isEmpty()) return

    val scheme = MaterialTheme.colorScheme

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = attachments,
            key = { it.stableKey }
        ) { attachment ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = scheme.surfaceVariant.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.35f))
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
                        tint = if (attachment.isImage) scheme.primary else scheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = attachment.displayName.take(20),
                        color = scheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
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
                            tint = scheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/* ---------------- Utility: URI to Attachment ---------------- */

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
        else -> "${"%.1f".format(fileSize / (1024.0 * 1024.0))} MB"
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
    } catch (e: Exception) {
        null
    }
}

/* ---------------- Top bar ---------------- */

@Composable
private fun ChatTopBar(
    provider: String,
    assistantStatus: String,
    isReady: Boolean,
    onClear: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = scheme.surface.copy(alpha = 0.92f),
        tonalElevation = 2.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssistantAvatar(
                size = 44.dp,
                ring = true
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Mr. Robot",
                        color = scheme.onSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.width(8.dp))

                    StatusDot(isReady = isReady)
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = if (isReady) "$provider  \u2022  $assistantStatus" else assistantStatus,
                    color = scheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TopBarIconButton(
                iconRes = R.drawable.ic_lucide_sparkles,
                contentDescription = "Clear chat",
                onClick = onClear
            )
        }
    }
}

@Composable
private fun AssistantAvatar(
    size: androidx.compose.ui.unit.Dp,
    ring: Boolean = false
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        scheme.primary,
                        scheme.tertiary
                    )
                )
            )
            .then(
                if (ring) Modifier.shadow(elevation = 2.dp, shape = CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_lucide_bot),
            contentDescription = null,
            tint = scheme.onPrimary,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

@Composable
private fun UserAvatar(size: androidx.compose.ui.unit.Dp) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(scheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_lucide_user),
            contentDescription = null,
            tint = scheme.onSecondaryContainer,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

@Composable
private fun StatusDot(isReady: Boolean) {
    val scheme = MaterialTheme.colorScheme
    val color = if (isReady) scheme.tertiary else scheme.error

    val transition = rememberInfiniteTransition(label = "status_pulse")
    val pulse by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (isReady) 1f else 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "status_pulse_alpha"
    )

    Box(
        modifier = Modifier
            .size(9.dp)
            .alpha(pulse)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun TopBarIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = scheme.surfaceVariant,
        border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.35f))
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = scheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/* ---------------- Status strip ---------------- */

@Composable
private fun StatusStrip(
    model: String,
    provider: String,
    isReady: Boolean,
    userMessages: Int,
    queuedFiles: Int,
    readableFiles: Int,
    visionImages: Int
) {
    val scheme = MaterialTheme.colorScheme
    val scroll = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoChip(
            iconRes = R.drawable.ic_lucide_cpu,
            label = if (isReady) model else "No model",
            tint = scheme.primary,
            emphasized = true
        )

        InfoChip(
            iconRes = R.drawable.ic_lucide_user,
            label = "$userMessages sent",
            tint = scheme.onSurfaceVariant
        )

        if (queuedFiles > 0) {
            InfoChip(
                iconRes = R.drawable.ic_lucide_folder,
                label = "$queuedFiles queued",
                tint = scheme.tertiary
            )
        }

        if (readableFiles > 0) {
            InfoChip(
                iconRes = R.drawable.ic_lucide_terminal,
                label = "$readableFiles readable",
                tint = scheme.tertiary
            )
        }

        if (visionImages > 0) {
            InfoChip(
                iconRes = R.drawable.ic_lucide_sparkles,
                label = "$visionImages vision",
                tint = scheme.secondary
            )
        }
    }
}

@Composable
private fun InfoChip(
    iconRes: Int,
    label: String,
    tint: Color,
    emphasized: Boolean = false
) {
    val scheme = MaterialTheme.colorScheme

    Surface(
        color = if (emphasized) tint.copy(alpha = 0.12f) else scheme.surface.copy(alpha = 0.88f),
        border = BorderStroke(
            width = 1.dp,
            color = if (emphasized) tint.copy(alpha = 0.45f) else scheme.outline.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(999.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = if (emphasized) tint else scheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

/* ---------------- Empty state ---------------- */

@Composable
private fun EmptyChatState(
    isReady: Boolean,
    onPrompt: (String) -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        AssistantAvatar(size = 72.dp, ring = true)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "How can I help you today?",
            color = scheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = if (isReady) {
                "Ask anything, attach files or screenshots, or dictate your prompt."
            } else {
                "Add an API key in Settings, then start a conversation."
            },
            color = scheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(22.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = scheme.surface.copy(alpha = 0.92f),
        border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.35f))
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
                    .background(scheme.primary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lucide_sparkles),
                    contentDescription = null,
                    tint = scheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = text,
                color = scheme.onSurface,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/* ---------------- Chat bubble ---------------- */

@Composable
private fun ChatBubble(
    message: ChatUiMessage
) {
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
            Spacer(Modifier.width(10.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 340.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Text(
                text = if (isUser) "You" else "Mr. Robot",
                color = scheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            val bubbleColor = if (isUser) {
                scheme.primary
            } else {
                scheme.surface.copy(alpha = 0.96f)
            }

            val textColor = if (isUser) scheme.onPrimary else scheme.onSurface

            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                color = bubbleColor,
                border = if (isUser) null else BorderStroke(
                    1.dp,
                    scheme.outline.copy(alpha = 0.3f)
                ),
                tonalElevation = if (isUser) 0.dp else 1.dp,
                shadowElevation = if (isUser) 2.dp else 0.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    MessageContent(
                        content = message.content,
                        textColor = textColor
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (message.attachments.isNotEmpty()) {
                    Text(
                        text = "${message.attachments.size} attachment${if (message.attachments.size == 1) "" else "s"}",
                        color = scheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "Copy",
                    color = scheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        clipboard.setText(AnnotatedString(message.content))
                    }
                )
            }
        }

        if (isUser) {
            Spacer(Modifier.width(10.dp))
            UserAvatar(size = 34.dp)
        }
    }
}

@Composable
private fun MessageContent(
    content: String,
    textColor: Color
) {
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
            color = scheme.surfaceVariant.copy(alpha = 0.6f),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.3f))
        ) {
            Text(
                text = content.replace("```", ""),
                color = textColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    } else {
        Text(
            text = content,
            color = textColor,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    }
}

/* ---------------- Thinking indicator ---------------- */

@Composable
private fun ThinkingBubble() {
    val scheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistantAvatar(size = 34.dp)
        Spacer(Modifier.width(10.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 4.dp,
                bottomEnd = 18.dp
            ),
            color = scheme.surface.copy(alpha = 0.96f),
            border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.3f)),
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThinkingDots(color = scheme.primary)

                Text(
                    text = "Thinking",
                    color = scheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ThinkingDots(color: Color) {
    val transition = rememberInfiniteTransition(label = "dots")
    val a1 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d1"
    )
    val a2 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, delayMillis = 150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d2"
    )
    val a3 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, delayMillis = 300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "d3"
    )

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Dot(color, a1)
        Dot(color, a2)
        Dot(color, a3)
    }
}

@Composable
private fun Dot(color: Color, alphaValue: Float) {
    Box(
        modifier = Modifier
            .size(7.dp)
            .alpha(alphaValue.coerceIn(0.35f, 1f))
            .clip(CircleShape)
            .background(color)
    )
}

/* ---------------- Error ---------------- */

@Composable
private fun ErrorPanel(
    message: String,
    onRetry: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = scheme.errorContainer.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, scheme.error.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(scheme.error.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = scheme.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Request failed",
                    color = scheme.onErrorContainer,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = message,
                    color = scheme.onErrorContainer.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(10.dp))

            Surface(
                modifier = Modifier.clickable { onRetry() },
                shape = RoundedCornerShape(999.dp),
                color = scheme.error.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, scheme.error.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "Retry",
                    color = scheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
    }
}

/* ---------------- Suggestion row ---------------- */

@Composable
private fun SuggestionRow(
    visible: Boolean,
    onPrompt: (String) -> Unit
) {
    val scheme = MaterialTheme.colorScheme

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
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SuggestionPrompts.forEach { prompt ->
                Surface(
                    modifier = Modifier.clickable { onPrompt(prompt) },
                    shape = RoundedCornerShape(999.dp),
                    color = scheme.primary.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, scheme.primary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = prompt,
                        color = scheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

/* ---------------- Composer ---------------- */

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
    val scheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp),
        color = scheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = scheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, scheme.outline.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
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
                        placeholder = {
                            Text(
                                text = "Message Mr. Robot...",
                                color = scheme.onSurfaceVariant,
                                fontSize = 15.sp
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 6,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            cursorColor = scheme.primary,
                            focusedTextColor = scheme.onSurface,
                            unfocusedTextColor = scheme.onSurface,
                            focusedPlaceholderColor = scheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = scheme.onSurfaceVariant
                        )
                    )

                    ComposerIconButton(
                        iconRes = R.drawable.ic_lucide_mic,
                        contentDescription = "Voice input",
                        onClick = onMicClick
                    )

                    SendButton(
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
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                tint = scheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(scheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeCount.coerceAtMost(9).toString(),
                    color = scheme.onPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SendButton(
    isLoading: Boolean,
    canSend: Boolean,
    canRegenerate: Boolean,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onRegenerate: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    val showRegenerate = !isLoading && !canSend && canRegenerate

    val bgColor = when {
        isLoading -> scheme.error
        showRegenerate -> scheme.surfaceVariant
        canSend -> scheme.primary
        else -> scheme.primary.copy(alpha = 0.45f)
    }

    val iconTint = when {
        isLoading -> scheme.onError
        showRegenerate -> scheme.onSurface
        else -> scheme.onPrimary
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

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(enabled = isLoading || canSend || showRegenerate) {
                when {
                    isLoading -> onStop()
                    canSend -> onSend()
                    showRegenerate -> onRegenerate()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = iconTint
            )
        } else {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = description,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/* ---------------- Voice input ---------------- */

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
