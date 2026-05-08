package com.mrrobot.aiworkspace.session

import com.mrrobot.aiworkspace.data.ChatMessage
import java.util.UUID

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val messages: List<ChatMessage> = emptyList()
)
