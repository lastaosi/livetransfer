package com.jh.livetransfer.util.audio_recorder

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 실시간 음성 스트리밍 처리 로직 (예시 뼈대)
    fun startRecording() {
        // AudioRecord 설정 및 데이터 수집 시작
    }

    fun stopRecording() {
        // 녹음 중지 및 리소스 해제
    }
}
