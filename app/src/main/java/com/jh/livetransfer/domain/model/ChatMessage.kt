package com.jh.livetransfer.domain.model

data class ChatMessage(
    val text: String,
    val isMine: Boolean,
    val languageCode : String
)
