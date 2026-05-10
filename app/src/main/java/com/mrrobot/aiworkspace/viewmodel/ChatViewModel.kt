package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.data.ChatMessage
import com.mrrobot.aiworkspace.data.ChatRepository
import com.mrrobot.aiworkspace.data.SettingsStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatAttachment(
    val id: Long = System.nanoTime(),
    val uri: String,
    val name: String,
    val mimeType: String,
    val sizeLabel: String = "",
    val imageDataUrl: String? = null,
    val extractedText: String = "",
    val extractionStatus: String = ""
) {
    val isImage: Boolean
        get() = mimeType.startsWith("image/")

    val isReadable: Boolean
        get() = extractedText.isNotBlank()
}

data class ChatUiMessage(
    val id: Long = System.nanoTime(),
    val role: String,
    val content: String,
    val modelContent: String = content,
    val attachments: List<ChatAttachment> = emptyList()
)

data class ChatUiState(
    val input: String = "",
    val messages: List<ChatUiMessage> = listOf(
        ChatUiMessage(
            role = "assistant",
            content = "Welcome to Mr. Robot AI Workspace. Attach an image, PDF, text file, code file, DOCX, or ZIP and ask me to explain or analyze it."
        )
    ),
    val selectedAttachments: List<ChatAttachment> = emptyList(),
    val apiKey: String = "",
    val model: String = "openai/gpt-4o-mini",
    val isLoading: Boolean = false,
    val error: String = "",
    val lastUserPrompt: String = "",
    val lastAttachments: List<ChatAttachment> = emptyList()
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStore = SettingsStore(application.applicationContext)
    private val repository = ChatRepository()
    private var activeJob: Job? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsStore.settingsFlow.collect { settings ->
                _uiState.value = _uiState.value.copy(
                    apiKey = settings.apiKey,
                    model = settings.model
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

    fun useSuggestion(prompt: String) {
        _uiState.value = _uiState.value.copy(
            input = prompt,
            error = ""
        )
    }

    fun addAttachments(attachments: List<ChatAttachment>) {
        if (attachments.isEmpty()) return

        val current = _uiState.value
        val merged = (current.selectedAttachments + attachments)
            .distinctBy { it.uri }
            .take(6)

        _uiState.value = current.copy(
            selectedAttachments = merged,
            error = ""
        )
    }

    fun removeAttachment(id: Long) {
        val current = _uiState.value

        _uiState.value = current.copy(
            selectedAttachments = current.selectedAttachments.filterNot { it.id == id },
            error = ""
        )
    }

    fun clearAttachments() {
        _uiState.value = _uiState.value.copy(
            selectedAttachments = emptyList(),
            error = ""
        )
    }

    fun send() {
        val current = _uiState.value
        val prompt = current.input.trim()

        if (prompt.isBlank() || current.isLoading) return

        sendPrompt(
            prompt = prompt,
            attachments = current.selectedAttachments,
            appendUserMessage = true
        )
    }

    fun retryLast() {
        val current = _uiState.value

        if (current.lastUserPrompt.isBlank() || current.isLoading) return

        _uiState.value = current.copy(error = "")

        sendPrompt(
            prompt = current.lastUserPrompt,
            attachments = current.lastAttachments,
            appendUserMessage = false
        )
    }

    fun regenerateLastAnswer() {
        val current = _uiState.value

        if (current.isLoading) return

        val lastUserMessage = current.messages.lastOrNull { it.role == "user" }
            ?: return

        val trimmedMessages = current.messages.dropLastWhile {
            it.role == "assistant"
        }

        _uiState.value = current.copy(
            messages = trimmedMessages,
            error = "",
            lastUserPrompt = lastUserMessage.content,
            lastAttachments = lastUserMessage.attachments
        )

        sendPrompt(
            prompt = lastUserMessage.content,
            attachments = lastUserMessage.attachments,
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

    fun clearChat() {
        activeJob?.cancel()
        activeJob = null

        _uiState.value = _uiState.value.copy(
            input = "",
            messages = listOf(
                ChatUiMessage(
                    role = "assistant",
                    content = "Chat cleared. Attach a file or ask your next Android development question."
                )
            ),
            selectedAttachments = emptyList(),
            isLoading = false,
            error = "",
            lastUserPrompt = "",
            lastAttachments = emptyList()
        )
    }

    private fun sendPrompt(
        prompt: String,
        attachments: List<ChatAttachment>,
        appendUserMessage: Boolean
    ) {
        val current = _uiState.value

        if (current.apiKey.isBlank()) {
            _uiState.value = current.copy(
                error = "OpenRouter API key is missing. Open Settings, paste your key, then save settings.",
                lastUserPrompt = prompt,
                lastAttachments = attachments
            )
            return
        }

        val modelContent = buildModelContent(
            prompt = prompt,
            attachments = attachments
        )

        val updatedMessages = if (appendUserMessage) {
            current.messages + ChatUiMessage(
                role = "user",
                content = prompt,
                modelContent = modelContent,
                attachments = attachments
            )
        } else {
            current.messages
        }

        _uiState.value = current.copy(
            input = "",
            selectedAttachments = if (appendUserMessage) emptyList() else current.selectedAttachments,
            messages = updatedMessages,
            isLoading = true,
            error = "",
            lastUserPrompt = prompt,
            lastAttachments = attachments
        )

        activeJob = viewModelScope.launch {
            val requestMessages = _uiState.value.messages
                .filter { it.role == "user" || it.role == "assistant" }
                .takeLast(10)
                .map {
                    ChatMessage(
                        role = it.role,
                        content = it.modelContent,
                        imageDataUrls = it.attachments.mapNotNull { attachment ->
                            attachment.imageDataUrl
                        }
                    )
                }

            val result = repository.sendMessage(
                apiKey = _uiState.value.apiKey,
                model = _uiState.value.model,
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
                    val hasImage = attachments.any { it.imageDataUrl != null }

                    val extraHint = if (hasImage) {
                        "\n\nIf this error says the model does not support image input, open Settings and select a vision-capable model."
                    } else {
                        ""
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = (throwable.message ?: "Unknown chat error") + extraHint
                    )
                }
        }
    }

    private fun buildModelContent(
        prompt: String,
        attachments: List<ChatAttachment>
    ): String {
        if (attachments.isEmpty()) return prompt

        val metadata = attachments.joinToString(separator = "\n") { attachment ->
            "- ${attachment.name} (${attachment.mimeType}${if (attachment.sizeLabel.isNotBlank()) ", ${attachment.sizeLabel}" else ""}) — ${attachment.extractionStatus.ifBlank { "metadata only" }}"
        }

        val readableText = attachments
            .filter { it.extractedText.isNotBlank() }
            .joinToString(separator = "\n\n") { attachment ->
                """
                ===== FILE: ${attachment.name} =====
                MIME: ${attachment.mimeType}
                SIZE: ${attachment.sizeLabel}
                STATUS: ${attachment.extractionStatus}

                ${attachment.extractedText}
                ===== END FILE: ${attachment.name} =====
                """.trimIndent()
            }

        val hasImages = attachments.any { it.imageDataUrl != null }

        val imageInstruction = if (hasImages) {
            """

            Actual image data is attached to this message. Analyze visible image content directly.
            """.trimIndent()
        } else {
            ""
        }

        val fileInstruction = if (readableText.isNotBlank()) {
            """

            Extracted readable file content is included below. Analyze this content directly. Do not say you cannot access the file.
            """.trimIndent()
        } else {
            """

            No readable text could be extracted from the selected file(s). Use metadata only and explain the limitation.
            """.trimIndent()
        }

        return """
        User request:
        $prompt

        Attached files:
        $metadata

        $imageInstruction

        $fileInstruction

        Extracted file content:
        $readableText
        """.trimIndent()
    }
}
