package com.mrrobot.aiworkspace.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrrobot.aiworkspace.ai.OpenRouterRepository
import com.mrrobot.aiworkspace.data.ChatMessage
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository =
        OpenRouterRepository()

    var apiKey by mutableStateOf("")
    var selectedModel by mutableStateOf(
        "openai/gpt-4o-mini"
    )

    var input by mutableStateOf("")

    var loading by mutableStateOf(false)

    var messages by mutableStateOf(
        listOf(
            ChatMessage(
                "assistant",
                "Welcome to Mr. Robot AI Workspace."
            )
        )
    )

    fun sendMessage() {

        if (
            input.isBlank() ||
            apiKey.isBlank()
        ) return

        val userMessage =
            ChatMessage(
                "user",
                input
            )

        messages = messages + userMessage

        val history = messages

        val currentInput = input

        input = ""

        loading = true

        viewModelScope.launch {

            val result =
                repository.sendMessage(
                    apiKey = apiKey,
                    model = selectedModel,
                    messages = history
                )

            loading = false

            result.onSuccess {

                messages =
                    messages + ChatMessage(
                        "assistant",
                        it
                    )
            }

            result.onFailure {

                messages =
                    messages + ChatMessage(
                        "assistant",
                        "Error: ${it.message}"
                    )
            }
        }
    }
}
