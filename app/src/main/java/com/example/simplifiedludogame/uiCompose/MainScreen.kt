package com.example.simplifiedludogame.uiCompose
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.simplifiedludogame.GameViewModel
import com.example.simplifiedludogame.preference.PreferenceHelper

@Composable
fun MainScreen(viewModel: GameViewModel, preferenceHelper: PreferenceHelper) {
    var playersSelected by remember { mutableStateOf(preferenceHelper.getPlayerCount() > 0) }

    if (!playersSelected) {
        PlayerSelectionScreen(viewModel = viewModel) {
            playersSelected = true
        }
    } else {
        GameScreen(viewModel = viewModel, onReset = {
            playersSelected = false
            preferenceHelper.clearPlayersSelected() // Clear state in SharedPreferences
        })
    }
}
