package com.jh.livetransfer.domain.usecase.translation

import com.jh.livetransfer.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TranslateAudioStreamUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
    suspend operator fun invoke(
        audioWavBytes: ByteArray,
        langA:String,
        langB:String
    ): Flow<String> {
        return repository.translateAudioStream(audioWavBytes,langA,langB)
    }
}