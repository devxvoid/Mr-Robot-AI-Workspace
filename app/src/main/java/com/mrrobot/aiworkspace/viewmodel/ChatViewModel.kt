package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.ai.OpenRouterRepository
import com.mrrobot.aiworkspace.data.ChatMessage
import com.mrrobot.aiworkspace.data.SettingsStore
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = OpenRouterRepository()
    private val settingsStore = SettingsStore(application.applicationContext)

    var apiKey by mutableStateOf("")
    var selectedModel by mutableStateOf("openai/gpt-4o-mini")
    var input by mutableStateOf("")
    var loading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    var messages by mutableStateOf(
        listOf(
            ChatMessage(
                role = "assistant",
                content = "Mr. Robot online. Add your OpenRouter API key in Settings, then send a message."
            )
        )
    )

    val currentMessages: List<ChatMessage>
        get() = messages

    init {
        viewModelScope.launch {
            settingsStore.settingsFlow.collect { settings ->
                apiKey = settings.apiKey
                selectedModel = settings.model.ifBlank {
                    "openai/gpt-4o-mini"
                }
            }
        }
    }

    fun sendMessage() {
        val prompt = input.trim()

        if (prompt.isBlank() || loading) return

        if (apiKey.isBlank()) {
            messages = messages + ChatMessage(
                role = "assistant",
                content = "API key missing. Open Settings and save your OpenRouter API key."
            )
            return
        }

        val userMessage = ChatMessage(
            role = "user",
            content = prompt
        )

        messages = messages + userMessage
        input = ""
        loading = true
        errorMessage = ""

        viewModelScope.launch {
            val result = repository.sendMessage(
                apiKey = apiKey,
                model = selectedModel,
                messages = messages
            )

            loading = false

            result.onSuccess { reply ->
                messages = messages + ChatMessage(
                    role = "assistant",
                    content = reply
                )
            }

            result.onFailure { error ->
                val message =
                    error.message ?: "Unknown AI error"

                errorMessage = message

                messages = messages + ChatMessage(
                    role = "assistant",
                    content = "AI error: $message"
                )
            }
        }
    }

    fun clear() {
        messages = listOf(
            ChatMessage(
                role = "assistant",
                content = "Chat cleared. Mr. Robot is ready."
            )
        )
    }

    fun clearChat() = clear()
    fun clearMessages() = clear()

    fun regenerate() {
        val lastUser =
            messages.lastOrNull { it.role == "user" } ?: return

        input = lastUser.content
        sendMessage()
    }

    fun regenerateLast() = regenerate()
}
