package com.jh.livetransfer.data.model

data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val isFinal: Boolean = false
)
