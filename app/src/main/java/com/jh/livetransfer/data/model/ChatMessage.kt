package com.jh.livetransfer.data.model

data class ChatMessage(
    val text: String,
    val isMine: Boolean,
    val languageCode : String
)
