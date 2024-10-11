package com.example.simplifiedludogame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplifiedludogame.preference.PreferenceHelper
import com.example.simplifiedludogame.uiCompose.MainScreen

class MainActivity : ComponentActivity() {
    private lateinit var preferenceHelper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the PreferenceHelper
        preferenceHelper = PreferenceHelper(this)

        // ViewModel factory to pass PreferenceHelper to GameViewModel
        val gameViewModel: GameViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(preferenceHelper) as T
                }
            }
        }
        setContent {
            MainScreen(viewModel = gameViewModel, preferenceHelper = preferenceHelper)
        }
    }
}

