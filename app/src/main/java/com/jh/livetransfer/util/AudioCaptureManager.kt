package com.jh.livetransfer.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioCaptureManager @Inject constructor(
     @ApplicationContext private val context: Context
) {
    // 실무 포인트 : AI 음성 인식(STT)나 Gemini API는 보통 16kHz,Mono 포맷을 잘인식함
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null

    // 멀티스레드 환경에서 녹음 상태를 안전하게 관맇ㅏrl dnlgo AtomicBoolean 사용
    private val isRecording = AtomicBoolean(false)

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(): Flow<ByteArray> = flow{
        // 버퍼 사이즈 계산 : 단말기 하드웨어에 맞는 최소 버퍼 크기를 가져옴
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat)

        // 권한 방어 로직(UI에서 체크하지만 시스템 안정성을 위해)
        if(ActivityCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            throw SecurityException("마이크 권한이 없습니다.")
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufferSize
        )
        audioRecord?.startRecording()
        isRecording.set(true)

        val buffer = ByteArray(minBufferSize)

        // flow 블록 내부에서 루프를 돌며 데이터를 방출(emit)
        while(isRecording.get() && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING){
            val readSize = audioRecord?.read(buffer,0,minBufferSize) ?: 0

            if(readSize >0 ){
                emit(buffer.copyOf(readSize))
            }
        }
    } .flowOn(Dispatchers.IO)

    @Synchronized
    fun stopRecording() {
        isRecording.set(false)
        audioRecord?.apply {
            if (recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                stop()
            }
            release()
        }
        audioRecord = null
    }

}
