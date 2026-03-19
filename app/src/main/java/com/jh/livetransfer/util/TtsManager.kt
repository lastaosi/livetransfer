package com.jh.livetransfer.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext context: Context
) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        // 객체가 생성될때 TTS 초기화 시작
        tts = TextToSpeech(context, this)

    }

    var onSpeakingStateChanged : ((Boolean)->Unit)? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    onSpeakingStateChanged?.invoke(true)
                }

                override fun onDone(utteranceId: String?) {
                    onSpeakingStateChanged?.invoke(false)
                }

                override fun onError(utteranceId: String?) {
                    onSpeakingStateChanged?.invoke(false)
                }

            })


            L.d("TTS 초기화 성공")
        } else {
            L.d("TTS 초기화 실패")
        }

    }

    fun speak(text: String,languageCode : String) {
        if (!isInitialized) {
            L.w("TTS is not initialized")
            return
        }
        // 💡 2. 문자열 언어 코드("en-US", "ko-KR" 등)를 Locale 객체로 변환하여 TTS에 세팅
        val locale = Locale.forLanguageTag(languageCode)
        val result = tts?.setLanguage(locale)

        // 💡 3. 해당 기기(스마트폰)에 그 언어의 음성 데이터가 없는 경우의 방어 코드
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            L.e("TTS 오류: 기기에서 지원하지 않는 언어입니다 ($languageCode)")
            // 필요하다면 여기에 토스트 메시지를 띄우거나, 기본 언어로 Fallback 처리할 수 있습니다.
            return
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH,null,"TTS_ID")


    }

    fun stop(){
        tts?.stop()
    }

    fun shutdown(){
        tts?.stop()
        tts?.shutdown()
        tts = null
    }


}