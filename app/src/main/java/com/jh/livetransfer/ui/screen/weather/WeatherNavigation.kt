package com.jh.livetransfer.ui.screen.weather

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun WeatherNavigation(viewModel : WeatherViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "weather_main"
        ){
        composable("weather_main"){
            WeatherMainScreen(
                viewModel,
                onSettingsClick = {
                    navController.navigate("weather_setting")

                }
            )
        }
        composable(route = "weather_settings"){
            WeatherSettingScreen(
                onSettingClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}