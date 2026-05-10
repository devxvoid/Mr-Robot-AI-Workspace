package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.data.ChatMessage
import com.mrrobot.aiworkspace.data.ChatRepository
import com.mrrobot.aiworkspace.data.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiMessage(
    val role: String,
    val content: String
)

data class ChatUiState(
    val input: String = "",
    val messages: List<ChatUiMessage> = listOf(
        ChatUiMessage(
            role = "assistant",
            content = "Mr. Robot online. Add your OpenRouter API key in Settings, then send a message."
        )
    ),
    val apiKey: String = "",
    val model: String = "openai/gpt-4o-mini",
    val isLoading: Boolean = false,
    val error: String = ""
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStore = SettingsStore(application.applicationContext)
    private val repository = ChatRepository()

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

    fun send() {
        val current = _uiState.value
        val prompt = current.input.trim()

        if (prompt.isBlank() || current.isLoading) return

        if (current.apiKey.isBlank()) {
            _uiState.value = current.copy(
                error = "OpenRouter API key missing. Save your key in Settings first."
            )
            return
        }

        val userMessage = ChatUiMessage("user", prompt)
        val updatedMessages = current.messages + userMessage

        _uiState.value = current.copy(
            input = "",
            messages = updatedMessages,
            isLoading = true,
            error = ""
        )

        viewModelScope.launch {
            val requestMessages = updatedMessages
                .filter { it.role == "user" || it.role == "assistant" }
                .map {
                    ChatMessage(
                        role = it.role,
                        content = it.content
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
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Unknown chat error"
                    )
                }
        }
    }

    fun clearChat() {
        _uiState.value = _uiState.value.copy(
            messages = listOf(
                ChatUiMessage(
                    role = "assistant",
                    content = "Chat cleared. Mr. Robot is ready."
                )
            ),
            error = "",
            input = ""
        )
    }
}
