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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        val config = generationConfig {
            // 필요에 따라 온도(temperature) 등을 설정할 수 있습니다.
        }

        // Firebase.vertexAI를 사용하여 모델을 가져옵니다.
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
