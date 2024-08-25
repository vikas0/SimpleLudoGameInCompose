package com.example.simplifiedludogame

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.simplifiedludogame.uiCompose.LudoBoard
import com.example.simplifiedludogame.uiCompose.LudoTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LudoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    LudoBoard(gameViewModel)
                }
            }
        }
    }
}
