package com.jh.livetransfer.ui.screen.translation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jh.livetransfer.ui.screen.SettingsScreen

@Composable
fun TranslationNavigation(viewModel: TranslationViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "translation_home"
    ) {
        composable("translation_home") {
            TranslationScreen(
                viewModel = viewModel,
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}