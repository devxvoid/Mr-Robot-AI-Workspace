package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.data.AppSettings
import com.mrrobot.aiworkspace.data.ChatMessage
import com.mrrobot.aiworkspace.data.ChatRepository
import com.mrrobot.aiworkspace.data.SettingsStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiMessage(
    val id: Long = System.currentTimeMillis(),
    val role: String,
    val content: String
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
                val activeModel = settings.activeModel()
                val activeKey = settings.activeApiKey()

                _uiState.value = _uiState.value.copy(
                    apiKey = activeKey,
                    model = activeModel,
                    provider = settings.selectedProvider.displayName,
                    isProviderReady = hasActive,
                    assistantStatus = if (hasActive) {
                        "${settings.selectedProvider.displayName} ready"
                    } else {
                        "No active model configured"
                    },
                    error = if (_uiState.value.error.contains("OpenRouter", ignoreCase = true)) {
                        ""
                    } else {
                        _uiState.value.error
                    }
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

        val lastUser = current.messages
            .lastOrNull { it.role == "user" }
            ?.content
            .orEmpty()

        if (lastUser.isBlank()) return

        val trimmedMessages = current.messages.dropLastWhile {
            it.role == "assistant"
        }

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

        val updatedMessages = if (appendUserMessage) {
            current.messages + ChatUiMessage(
                role = "user",
                content = prompt
            )
        } else {
            current.messages
        }

        _uiState.value = current.copy(
            input = "",
            messages = updatedMessages,
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
                    if (throwable is kotlinx.coroutines.CancellationException) {
                        return@onFailure
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "AI request failed."
                    )
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
            isLoading = false,
            lastUserPrompt = ""
        )
    }
}
