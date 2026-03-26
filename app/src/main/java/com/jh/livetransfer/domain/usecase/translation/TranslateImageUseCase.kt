package com.jh.livetransfer.domain.usecase.translation

import com.jh.livetransfer.domain.repository.TranslationRepository
import javax.inject.Inject

class TranslateImageUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        langA: String,
        langB: String
    ): String {
        return repository.translateImage(imageBytes, langA, langB)
    }
}