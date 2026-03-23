package com.jh.livetransfer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jh.livetransfer.ui.screen.exchangerate.ExchangeRateScreen
import com.jh.livetransfer.ui.screen.translation.TranslationNavigation
import com.jh.livetransfer.ui.screen.translation.TranslationScreen
import com.jh.livetransfer.ui.screen.translation.TranslationViewModel
import com.jh.livetransfer.ui.screen.weather.WeatherNavigation
import com.jh.livetransfer.ui.screen.weather.WeatherViewModel
import com.jh.livetransfer.ui.theme.LiveTransferTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveTransferTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
private fun AppNavigation() {
    val navController = rememberNavController()

    val items = listOf(BottomNavItem.Translation, BottomNavItem.Weather, BottomNavItem.Exchange)
    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {Icon(item.icon, contentDescription = item.label)},
                        label = {Text(item.label)},
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route){
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) {paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Translation.route,
            modifier = Modifier.padding(paddingValues)
        ){
            composable(BottomNavItem.Translation.route){
                val viewModel: TranslationViewModel = hiltViewModel()
                TranslationNavigation(viewModel = viewModel)
            }

            composable(BottomNavItem.Exchange.route){
                ExchangeRateScreen()
            }
            composable(BottomNavItem.Weather.route){
                val viewModel: WeatherViewModel = hiltViewModel()
                WeatherNavigation(viewModel = viewModel)
            }
        }
    }

}


sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Translation : BottomNavItem(
        route = "translation",
        label = "번역",
        icon = Icons.Default.Translate
    )

    object Weather : BottomNavItem(
        route = "weather",
        label = "날씨",
        icon = Icons.Default.Cloud
    )

    object Exchange : BottomNavItem("exchange", "환율", Icons.Outlined.CurrencyExchange)
}
