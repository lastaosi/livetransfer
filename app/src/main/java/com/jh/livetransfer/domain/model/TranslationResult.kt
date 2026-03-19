package com.jh.livetransfer.domain.model

data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val isFinal: Boolean = false
)
