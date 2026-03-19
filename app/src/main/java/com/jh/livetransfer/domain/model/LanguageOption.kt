package com.jh.livetransfer.domain.model

data class LanguageOption(val name: String, val code: String)
val SUPPORTED_LANGUAGES = listOf(
    LanguageOption("한국어", "ko"),
    LanguageOption("영어", "en"),
    LanguageOption("일본어", "ja"),
    LanguageOption("중국어", "zh"),
    LanguageOption("스페인어", "es"),
    LanguageOption("프랑스어", "fr"),
    LanguageOption("독일어", "de")
)
