package com.jh.livetransfer.domain.usecase

import com.jh.livetransfer.domain.repository.TranslationRepository
import javax.inject.Inject

class GetRealtimeTranslationUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
//    suspend operator fun invoke(audioData: ByteArray): String {
//        return repository.translateAudio(audioData,"",langB = "")
//    }
}
