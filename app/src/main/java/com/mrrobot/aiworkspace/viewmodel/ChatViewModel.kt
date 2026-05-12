package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.data.AppSettings
import com.mrrobot.aiworkspace.data.ChatMessage
import com.mrrobot.aiworkspace.data.ChatRepository
import com.mrrobot.aiworkspace.data.SettingsStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatAttachment(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri? = null,
    val name: String = "Attachment",
    val displayName: String = name,
    val mimeType: String = "",
    val sizeBytes: Long = 0L,
    val sizeLabel: String = "",
    val type: String = "",
    val readableText: String = "",
    val extractedText: String = "",
    val description: String = "",
    val imageDataUrl: String = "",
    val extractionStatus: String = "",
    val isImage: Boolean = mimeType.startsWith("image/") || imageDataUrl.isNotBlank(),
    val isReadable: Boolean = readableText.isNotBlank() || extractedText.isNotBlank()
) {
    constructor(
        uri: Uri?,
        name: String,
        mimeType: String = "",
        sizeBytes: Long = 0L,
        imageDataUrl: String = "",
        extractionStatus: String = ""
    ) : this(
        id = UUID.randomUUID().toString(),
        uri = uri,
        name = name,
        displayName = name,
        mimeType = mimeType,
        sizeBytes = sizeBytes,
        imageDataUrl = imageDataUrl,
        extractionStatus = extractionStatus
    )
}

data class ChatUiMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String,
    val attachments: List<ChatAttachment> = emptyList()
)

data class ChatUiState(
    val input: String = "",
    val messages: List<ChatUiMessage> = listOf(
        ChatUiMessage(
            role = "assistant",
            content = "Mr. Robot online. Add and activate any supported AI provider in Settings, then send a message."
        )
    ),
    val apiKey: String = "",
    val model: String = "openai/gpt-4o-mini",
    val provider: String = "No active model",
    val assistantStatus: String = "No active model configured",
    val isProviderReady: Boolean = false,
    val isLoading: Boolean = false,
    val error: String = "",
    val lastUserPrompt: String = "",
    val selectedAttachments: List<ChatAttachment> = emptyList(),
    val queuedFiles: Int = 0,
    val readableFiles: Int = 0,
    val visionImages: Int = 0
) {
    val userMessages: Int
        get() = messages.count { it.role == "user" }
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStore = SettingsStore(application.applicationContext)
    private val repository = ChatRepository()

    private var activeJob: Job? = null
    private var activeSettings: AppSettings = AppSettings()

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsStore.settingsFlow.collect { settings ->
                activeSettings = settings

                val hasActive = settings.hasActiveConfiguration()
                val activeKey = settings.activeApiKey()
                val activeModel = settings.activeModel()

                _uiState.value = _uiState.value.copy(
                    apiKey = activeKey,
                    model = activeModel,
                    provider = if (hasActive) settings.selectedProvider.displayName else "No active model",
                    isProviderReady = hasActive,
                    assistantStatus = if (hasActive) {
                        "${settings.selectedProvider.displayName} ready"
                    } else {
                        "No active model configured"
                    },
                    error = _uiState.value.error
                        .takeUnless { it.contains("OpenRouter", ignoreCase = true) }
                        .orEmpty()
                )
            }
        }
    }

    fun updateInput(value: String) {
        _uiState.value = _uiState.value.copy(
            input = value,
            error = ""
        )
    }

    fun useSuggestion(suggestion: String) {
        _uiState.value = _uiState.value.copy(
            input = suggestion,
            error = ""
        )
    }

    fun addAttachments(items: List<ChatAttachment>) {
        if (items.isEmpty()) return

        val merged = (_uiState.value.selectedAttachments + items).distinctBy { it.id }

        _uiState.value = _uiState.value.copy(
            selectedAttachments = merged,
            queuedFiles = merged.size,
            readableFiles = merged.count { it.isReadable },
            visionImages = merged.count { it.isImage },
            error = ""
        )
    }

    fun removeAttachment(attachment: ChatAttachment) {
        val updated = _uiState.value.selectedAttachments.filterNot { it.id == attachment.id }

        _uiState.value = _uiState.value.copy(
            selectedAttachments = updated,
            queuedFiles = updated.size,
            readableFiles = updated.count { it.isReadable },
            visionImages = updated.count { it.isImage }
        )
    }

    fun removeAttachmentById(id: String) {
        val updated = _uiState.value.selectedAttachments.filterNot { it.id == id }

        _uiState.value = _uiState.value.copy(
            selectedAttachments = updated,
            queuedFiles = updated.size,
            readableFiles = updated.count { it.isReadable },
            visionImages = updated.count { it.isImage }
        )
    }

    fun send() {
        val current = _uiState.value
        val prompt = current.input.trim()

        if (prompt.isBlank() || current.isLoading) return

        sendPrompt(
            prompt = prompt,
            appendUserMessage = true
        )
    }

    fun retryLast() {
        val prompt = _uiState.value.lastUserPrompt

        if (prompt.isBlank() || _uiState.value.isLoading) return

        _uiState.value = _uiState.value.copy(error = "")

        sendPrompt(
            prompt = prompt,
            appendUserMessage = false
        )
    }

    fun regenerateLastAnswer() {
        val current = _uiState.value

        if (current.isLoading) return

        val lastUser = current.messages.lastOrNull { it.role == "user" }?.content.orEmpty()

        if (lastUser.isBlank()) return

        val trimmedMessages = current.messages.dropLastWhile { it.role == "assistant" }

        _uiState.value = current.copy(
            messages = trimmedMessages,
            error = "",
            lastUserPrompt = lastUser
        )

        sendPrompt(
            prompt = lastUser,
            appendUserMessage = false
        )
    }

    fun stopGeneration() {
        activeJob?.cancel()
        activeJob = null

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = "Generation stopped."
        )
    }

    private fun sendPrompt(
        prompt: String,
        appendUserMessage: Boolean
    ) {
        val settings = activeSettings
        val current = _uiState.value

        if (!settings.hasActiveConfiguration()) {
            _uiState.value = current.copy(
                error = "No active AI model. Open Settings, add an API key, then tap Save & Activate."
            )
            return
        }

        val attachmentContext = buildAttachmentContext(current.selectedAttachments)
        val finalPrompt = if (attachmentContext.isBlank()) {
            prompt
        } else {
            "$prompt\n\nAttached context:\n$attachmentContext"
        }

        val updatedMessages = if (appendUserMessage) {
            current.messages + ChatUiMessage(
                role = "user",
                content = prompt,
                attachments = current.selectedAttachments
            )
        } else {
            current.messages
        }

        _uiState.value = current.copy(
            input = "",
            messages = updatedMessages,
            selectedAttachments = emptyList(),
            queuedFiles = 0,
            readableFiles = 0,
            visionImages = 0,
            isLoading = true,
            error = "",
            lastUserPrompt = prompt,
            apiKey = settings.activeApiKey(),
            model = settings.activeModel(),
            provider = settings.selectedProvider.displayName,
            assistantStatus = "${settings.selectedProvider.displayName} ready",
            isProviderReady = true
        )

        activeJob = viewModelScope.launch {
            val requestMessages = _uiState.value.messages
                .filter { it.role == "user" || it.role == "assistant" }
                .map {
                    ChatMessage(
                        role = it.role,
                        content = it.content
                    )
                }
                .toMutableList()

            if (requestMessages.isNotEmpty() && finalPrompt != prompt) {
                val lastIndex = requestMessages.indexOfLast { it.role == "user" }
                if (lastIndex >= 0) {
                    requestMessages[lastIndex] = requestMessages[lastIndex].copy(
                        content = finalPrompt
                    )
                }
            }

            val result = repository.sendMessage(
                settings = settings,
                messages = requestMessages
            )

            result
                .onSuccess { reply ->
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + ChatUiMessage(
                            role = "assistant",
                            content = reply
                        ),
                        isLoading = false,
                        error = ""
                    )
                }
                .onFailure { throwable ->
                    if (throwable is CancellationException) return@onFailure

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "AI request failed."
                    )
                }
        }
    }

    private fun buildAttachmentContext(attachments: List<ChatAttachment>): String {
        if (attachments.isEmpty()) return ""

        return attachments.joinToString(separator = "\n\n") { attachment ->
            val text = attachment.readableText
                .ifBlank { attachment.extractedText }
                .ifBlank { attachment.description }

            buildString {
                append("- Name: ")
                append(attachment.displayName.ifBlank { attachment.name })

                if (attachment.mimeType.isNotBlank()) {
                    append("\n  MIME: ")
                    append(attachment.mimeType)
                }

                if (attachment.sizeBytes > 0L) {
                    append("\n  Size: ")
                    append(attachment.sizeBytes)
                    append(" bytes")
                }

                if (attachment.imageDataUrl.isNotBlank()) {
                    append("\n  Image: attached")
                }

                if (attachment.extractionStatus.isNotBlank()) {
                    append("\n  Status: ")
                    append(attachment.extractionStatus)
                }

                if (text.isNotBlank()) {
                    append("\n  Content: ")
                    append(text.take(8000))
                } else {
                    append("\n  Content: File attached but no readable text was extracted yet.")
                }
            }
        }
    }

    fun clearChat() {
        activeJob?.cancel()
        activeJob = null

        _uiState.value = _uiState.value.copy(
            messages = listOf(
                ChatUiMessage(
                    role = "assistant",
                    content = "Chat cleared. Mr. Robot is ready."
                )
            ),
            error = "",
            input = "",
            selectedAttachments = emptyList(),
            queuedFiles = 0,
            readableFiles = 0,
            visionImages = 0,
            isLoading = false,
            lastUserPrompt = ""
        )
    }
}
