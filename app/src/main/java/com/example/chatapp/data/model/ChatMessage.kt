package com.example.chatapp.data.model

data class ChatMessage(
    val message: String,
    val isSent: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
