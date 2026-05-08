package com.mrrobot.aiworkspace.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.ai.OpenRouterRepository
import com.mrrobot.aiworkspace.data.ChatMessage
import com.mrrobot.aiworkspace.session.ChatSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = OpenRouterRepository()

    var apiKey by mutableStateOf("")
    var selectedModel by mutableStateOf("openai/gpt-4o-mini")
    var input by mutableStateOf("")
    var loading by mutableStateOf(false)

    var sessions by mutableStateOf(
        listOf(
            ChatSession(
                title = "Main Chat",
                messages = listOf(
                    ChatMessage(
                        role = "assistant",
                        content = "Welcome to Mr. Robot AI Workspace."
                    )
                )
            )
        )
    )

    var selectedSessionId by mutableStateOf(
        sessions.first().id
    )

    val currentMessages: List<ChatMessage>
        get() =
            sessions
                .firstOrNull { it.id == selectedSessionId }
                ?.messages
                ?: emptyList()

    fun createSession() {
        val session = ChatSession(
            title = "Chat ${sessions.size + 1}",
            messages = listOf(
                ChatMessage(
                    role = "assistant",
                    content = "New workspace chat created."
                )
            )
        )

        sessions = listOf(session) + sessions
        selectedSessionId = session.id
    }

    fun switchSession(id: String) {
        selectedSessionId = id
        input = ""
    }

    fun deleteCurrentSession() {
        if (sessions.size <= 1) return

        sessions = sessions.filterNot {
            it.id == selectedSessionId
        }

        selectedSessionId = sessions.first().id
    }

    fun clearCurrentChat() {
        updateCurrentMessages(
            listOf(
                ChatMessage(
                    role = "assistant",
                    content = "Chat cleared. Mr. Robot is ready."
                )
            )
        )
    }

    fun sendMessage() {
        val prompt = input.trim()

        if (prompt.isBlank() || loading) return

        if (apiKey.isBlank()) {
            appendMessage(
                ChatMessage(
                    role = "assistant",
                    content = "Please enter your OpenRouter API key first."
                )
            )
            return
        }

        appendMessage(
            ChatMessage(
                role = "user",
                content = prompt
            )
        )

        input = ""
        loading = true

        viewModelScope.launch {
            val result = repository.sendMessage(
                apiKey = apiKey,
                model = selectedModel,
                messages = currentMessages
            )

            loading = false

            result.onSuccess { response ->
                appendStreamingResponse(response)
            }

            result.onFailure { error ->
                appendMessage(
                    ChatMessage(
                        role = "assistant",
                        content = "Error: ${error.message ?: "Unknown error"}"
                    )
                )
            }
        }
    }

    private suspend fun appendStreamingResponse(response: String) {
        appendMessage(
            ChatMessage(
                role = "assistant",
                content = ""
            )
        )

        var streamed = ""

        response.forEach { char ->
            delay(6)
            streamed += char

            val messages = currentMessages.toMutableList()

            if (messages.isNotEmpty()) {
                messages[messages.lastIndex] =
                    ChatMessage(
                        role = "assistant",
                        content = streamed
                    )
                updateCurrentMessages(messages)
            }
        }
    }

    private fun appendMessage(message: ChatMessage) {
        updateCurrentMessages(
            currentMessages + message
        )
    }

    private fun updateCurrentMessages(
        messages: List<ChatMessage>
    ) {
        sessions = sessions.map { session ->
            if (session.id == selectedSessionId) {
                session.copy(messages = messages)
            } else {
                session
            }
        }
    }
}
