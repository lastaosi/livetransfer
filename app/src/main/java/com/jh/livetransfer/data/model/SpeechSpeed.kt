package com.jh.livetransfer.data.model

enum class SpeechSpeed(val label: String,val silenceDelay: Long) {
    FAST("빠름",600L),

    NORMAL("보통",1000L),
    SLOW("느림",1500L)
}