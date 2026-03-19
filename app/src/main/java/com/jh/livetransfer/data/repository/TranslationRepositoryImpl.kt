package com.jh.livetransfer.data.repository

import android.graphics.BitmapFactory
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.content
import com.jh.livetransfer.domain.repository.TranslationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel
) : TranslationRepository {

    override suspend fun translateAudio(audioBytes: ByteArray, langA: String, langB: String): String =
        withContext(Dispatchers.IO) {
            val response = generativeModel.generateContent(
                content {
                    text("""
                    [Task]
                    Identify the language of the audio.
                    - If it is '$langA', translate it to '$langB'.
                    - If it is '$langB', translate it to '$langA'.

                    [Input Audio]
                """.trimIndent())
                    blob("audio/wav", audioBytes)
                }
            )
            response.text?.trim() ?: ""
        }

    // 예외는 caller(ViewModel)로 전파 — Repository가 에러를 텍스트로 emit하지 않음
    override suspend fun translateAudioStream(
        audioWavBytes: ByteArray,
        langA: String,
        langB: String
    ): Flow<String> = flow {
        val responseStream = generativeModel.generateContentStream(
            content {
                text("입력된 음성을 분석해서, 만약 ${langA}라면 ${langB}로 번역하고, ${langB}라면 ${langA}로 번역해.")
                blob("audio/wav", audioWavBytes)
            }
        )
        responseStream.collect { chunk ->
            chunk.text?.let { emit(it) }
        }
    }

    override suspend fun translateImage(imageBytes: ByteArray, langA: String, langB: String): String {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val response = generativeModel.generateContent(
            content {
                text("""
                    You are an expert OCR and translation AI.
                    Your task is to extract text from the image and translate it.

                    [STRICT RULES]
                    1. Extract ALL visible text from the provided image.
                    2. Detect the language of the extracted text.
                    3. If the text is in '$langA', translate it to '$langB'.
                    4. If the text is in '$langB', translate it to '$langA'.
                    5. If the text is mixed, translate it based on context.
                    6. **OUTPUT ONLY THE TRANSLATED TEXT.**
                    7. Do NOT include the original text or any explanations (e.g., "Translation:", "Here is the text").
                    8. If no text is found, output "No text detected in the image."
                """.trimIndent())
                image(bitmap)
            }
        )
        return response.text ?: ""
    }
}
