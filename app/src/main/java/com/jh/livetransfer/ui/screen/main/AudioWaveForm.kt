package com.jh.livetransfer.ui.screen.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun AudioWaveform(
    amplitudes: List<Float>, // 0.0 ~ 1.0 사이의 값 리스트
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF6200EE) // 보라색 (원하는 색으로 변경 가능)
) {
    Canvas(
        modifier = modifier.height(80.dp)
    ) {
        val barWidth = 10.dp.toPx()
        val gap = 4.dp.toPx()
        val centerY = size.height / 2f
        val startX = 0f // 왼쪽부터 그림

        amplitudes.forEachIndexed { index, amp ->
            // 높이 계산 (최소 높이 보장)
            val barHeight = (size.height * amp * 0.8f).coerceAtLeast(10f)
            val x = startX + index * (barWidth + gap)

            drawLine(
                color = barColor,
                start = Offset(x = x, y = centerY - barHeight / 2),
                end = Offset(x = x, y = centerY + barHeight / 2),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}