package com.jh.livetransfer.domain.repository

import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    suspend fun translateAudio(audioBytes: ByteArray,langA: String,langB: String):String

    suspend fun translateAudioStream(audioWavBytes: ByteArray, langA: String, langB: String): Flow<String>

    suspend fun translateImage(imageBytes: ByteArray, langA: String, langB: String): String
}
