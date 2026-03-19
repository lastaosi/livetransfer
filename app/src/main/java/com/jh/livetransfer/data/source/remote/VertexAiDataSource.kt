package com.jh.livetransfer.data.source.remote

import com.google.firebase.vertexai.GenerativeModel
import com.jh.livetransfer.domain.model.TranslationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VertexAiDataSource @Inject constructor(
    private val generativeModel: GenerativeModel
) {
    fun getTranslationFlow(audioData: ByteArray): Flow<TranslationResult> = flow {
        // Vertex AI 실시간 번역 로직 (예시 코드)
        emit(TranslationResult("Original Text Sample", "번역 텍스트 샘플", true))
    }
}
