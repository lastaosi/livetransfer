package com.jh.livetransfer.ui.screen.weather

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * 날씨 탭 내부 중첩 네비게이션.
 *
 * 라우트:
 * - "weather_main": 현재 위치 + 추가 도시 날씨 목록
 * - "weather_settings": 날씨 설정 화면 (현재 stub 상태)
 *
 * 주의: 메인 화면에서 navigate("weather_setting")으로 이동하지만,
 * 실제 composable 등록은 "weather_settings" (복수형)로 되어 있어 경로 불일치 버그 존재.
 */
@Composable
fun WeatherNavigation(viewModel: WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "weather_main"
    ) {
        composable("weather_main") {
            WeatherMainScreen(
                viewModel,
                onSettingsClick = {
                    navController.navigate("weather_setting")
                }
            )
        }
        // TODO: "weather_setting"과 "weather_settings" 경로 불일치 수정 필요
        composable(route = "weather_settings") {
            WeatherSettingScreen(
                onSettingClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}