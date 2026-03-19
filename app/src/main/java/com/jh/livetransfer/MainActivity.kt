package com.jh.livetransfer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jh.livetransfer.ui.screen.SettingsScreen
import com.jh.livetransfer.ui.screen.main.MainScreen
import com.jh.livetransfer.ui.screen.main.MainViewModel
import com.jh.livetransfer.ui.screen.translate.TranslateScreen
import com.jh.livetransfer.ui.screen.translate.TranslateViewModel
import com.jh.livetransfer.ui.theme.LiveTransferTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveTransferTheme {
                val navController = rememberNavController()
                var isSettingsScreen by remember { mutableStateOf(false) }
                val viewModel: MainViewModel = hiltViewModel()
                if(isSettingsScreen){
                    SettingsScreen(viewModel, onBackClick = {isSettingsScreen = false})
                }else{

                    MainScreen(
                        viewModel = viewModel,
                        onSettingsClick = { isSettingsScreen = true }
                    )
                }

            }
        }
    }
}
