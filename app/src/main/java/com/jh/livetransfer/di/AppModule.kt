package com.jh.livetransfer.di

import com.google.firebase.Firebase
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 앱 전역 DI 모듈.
 * Hilt SingletonComponent에 설치되어 앱 생명주기 동안 단 하나의 인스턴스를 유지한다.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Gemini 2.5 Flash 모델 싱글톤 제공.
     *
     * - Firebase Vertex AI SDK 사용 (google-services.json 필수)
     * - systemInstruction: 모든 요청에 공통으로 적용되는 전문 통역사 페르소나
     *   → "번역문만 출력"을 강제해 UI에 불필요한 부가 텍스트가 섞이는 것을 방지
     */
    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        val config = generationConfig {
            // 필요에 따라 temperature, topK, topP 등 생성 파라미터 조정 가능
        }

        return Firebase.vertexAI.generativeModel(
            modelName = "gemini-2.5-flash",
            generationConfig = config,
            systemInstruction = com.google.firebase.vertexai.type.content {
                text("""
                    You are a professional real-time interpreter.
                    Your mission is to translate audio input accurately and instantly.

                    **STRICT RULES:**
                    1. Output ONLY the translated text.
                    2. DO NOT add any conversational fillers like "Here is the translation", "Translated:", or "Okay".
                    3. DO NOT explain the translation or provide pronunciation guides.
                    4. If the audio is silence or unintelligible noise, output nothing (empty string).
                    5. Maintain the original tone and nuance of the speaker.
                """.trimIndent())
            }
        )
    }
}
