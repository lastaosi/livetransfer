package com.jh.livetransfer.ui.screen.weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * 날씨 설정 화면. [STUB / 미구현]
 * 현재 "준비 중" 텍스트만 표시. 향후 도시 관리 UI 등을 추가할 화면.
 */
@Composable
fun WeatherSettingScreen(
    onSettingClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "날씨 설정 화면 (준비 중)",
            color = Color.Gray
        )
    }
}
