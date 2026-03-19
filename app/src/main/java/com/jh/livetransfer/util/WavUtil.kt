package com.jh.livetransfer.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun ByteArray.pcmToWav(sampleRate: Int = 16000): ByteArray {
    val headerSize = 44
    val audioDataSize = this.size
    val totalSize = headerSize + audioDataSize
    val buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.LITTLE_ENDIAN)

    // RIFF Header
    buffer.put("RIFF".toByteArray())
    buffer.putInt(totalSize - 8)
    buffer.put("WAVE".toByteArray())

    // format chunk
    buffer.put("fmt ".toByteArray())
    buffer.putInt(16) // Subchunk1Size for PCM
    buffer.putShort(1.toShort()) // AudioFormat (1 = PCM)
    buffer.putShort(1.toShort()) // NumChannels (1 = Mono)
    buffer.putInt(sampleRate)
    buffer.putInt(sampleRate * 1 * 16 / 8) // ByteRate
    buffer.putShort(2.toShort()) // BlockAlign (NumChannels * BitsPerSample / 8)
    buffer.putShort(16.toShort()) // BitsPerSample

    // data chunk
    buffer.put("data".toByteArray())
    buffer.putInt(audioDataSize)
    buffer.put(this)

    return buffer.array()
}
