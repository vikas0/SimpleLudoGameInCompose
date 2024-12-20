package com.example.simplifiedludogame


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.simplifiedludogame.model.GameState
import com.example.simplifiedludogame.uiCompose.GameScreen
import com.example.simplifiedludogame.uiCompose.PlayerSelectionScreen

@Composable
fun GameFlowScreen(
    gameViewModel: GameViewModel,
    onExitToMenu: () -> Unit,
    onManageTokens: () -> Unit
) {
    val gameState by gameViewModel.gameState

    when (gameState) {
        is GameState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is GameState.PlayerSelection -> {
            PlayerSelectionScreen(gameViewModel) {
                gameViewModel.startNewGame()
            }
        }

        is GameState.ShowSavedGameDialog -> {
            ResumeGameDialog(
                onResumeGame = { gameViewModel.resumeGame() },
                onStartNewGame = { gameViewModel.startNewGame() }
            )
        }

        is GameState.InGame -> {
            GameScreen(
                viewModel = gameViewModel,
                onExitToMenu = onExitToMenu,
                onManageTokens = onManageTokens
            )
        }
    }
}



@Composable
fun ResumeGameDialog(
    onResumeGame: () -> Unit,
    onStartNewGame: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Prevent dismiss */ },
        title = { Text(text = "Resume Game") },
        text = { Text(text = "A previous game was found. Would you like to resume or start a new game?") },
        confirmButton = {
            TextButton(onClick = onResumeGame) {
                Text("Resume")
            }
        },
        dismissButton = {
            TextButton(onClick = onStartNewGame) {
                Text("Start New Game")
            }
        }
    )
}
