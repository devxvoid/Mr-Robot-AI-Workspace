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
            content = "Mr. Robot online. Add your OpenRouter API key in Settings, then send a message."
        )
    ),
    val apiKey: String = "",
    val model: String = "openai/gpt-4o-mini",
    val isLoading: Boolean = false,
    val error: String = "",
    val lastUserPrompt: String = ""
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

    fun send() {
        val current = _uiState.value
        val prompt = current.input.trim()

        if (prompt.isBlank() || current.isLoading) return

        sendPrompt(prompt, appendUserMessage = true)
    }

    fun retryLast() {
        val prompt = _uiState.value.lastUserPrompt
        if (prompt.isBlank() || _uiState.value.isLoading) return

        _uiState.value = _uiState.value.copy(error = "")
        sendPrompt(prompt, appendUserMessage = false)
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

        sendPrompt(lastUser, appendUserMessage = false)
    }

    fun stopGeneration() {
        activeJob?.cancel()
        activeJob = null

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = "Generation stopped."
        )
    }

    private fun sendPrompt(prompt: String, appendUserMessage: Boolean) {
        val current = _uiState.value

        if (current.apiKey.isBlank()) {
            _uiState.value = current.copy(
                error = "OpenRouter API key missing. Save your key in Settings first."
            )
            return
        }

        val updatedMessages =
            if (appendUserMessage) {
                current.messages + ChatUiMessage(role = "user", content = prompt)
            } else {
                current.messages
            }

        _uiState.value = current.copy(
            input = "",
            messages = updatedMessages,
            isLoading = true,
            error = "",
            lastUserPrompt = prompt
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
                    if (throwable is kotlinx.coroutines.CancellationException) {
                        return@onFailure
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Unknown chat error"
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
