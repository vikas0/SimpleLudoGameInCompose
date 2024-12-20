package com.example.simplifiedludogame.uiCompose

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import com.example.simplifiedludogame.GameViewModel
import com.example.simplifiedludogame.model.Player

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onExitToMenu: () -> Unit,
    onManageTokens: () -> Unit
) {
    val context = LocalContext.current

    // Observe toast message from ViewModel
    val toastMessage = viewModel.toastMessage.value

    // Display toast when a message is set
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.toastMessage.value = null // Clear the toast message after showing
        }
    }

    val isGameOver = viewModel.isGameOver.value
    val winner = viewModel.winner.value

    Scaffold(
        topBar = { GameScreenTopBar(onExitToMenu, onManageTokens) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                GameBoardSection(viewModel)
                GameActionsSection(viewModel, context, onExitToMenu)
                if (isGameOver) GameOverSection(viewModel, winner)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenTopBar(
    onExitToMenu: () -> Unit,
    onManageTokens: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Ludo Game") },
        actions = {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Exit to Menu") },
                    onClick = {
                        showMenu = false
                        onExitToMenu()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Manage Tokens") },
                    onClick = {
                        showMenu = false
                        onManageTokens()
                    }
                )
            }
        }
    )
}


@Composable
fun GameBoardSection(viewModel: GameViewModel) {
    Text(text = "Ludo Board")
    Spacer(modifier = Modifier.height(8.dp))
    LudoBoard(viewModel = viewModel) // Assumes LudoBoard is a separate composable
    Spacer(modifier = Modifier.height(16.dp))
    DiceRollAndMove(viewModel = viewModel) // Assumes DiceRollAndMove is a separate composable
}

@Composable
fun GameActionsSection(
    viewModel: GameViewModel,
    context: android.content.Context,
    onExitToMenu: () -> Unit
) {
    HorizontalDivider(
        color = Color.Gray,
        thickness = 1.5.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    // Scoreboard Section
    ScoreBoard(viewModel = viewModel) // Assumes ScoreBoard is a separate composable

    HorizontalDivider(
        color = Color.Gray,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    // Save and Exit & Reset Buttons
    GameActionButtons(
        onSaveAndExit = {
            viewModel.saveGameState()
            Toast.makeText(context, "Game Saved!", Toast.LENGTH_SHORT).show()
            onExitToMenu()
        },
        onResetGame = {
            viewModel.resetGame()
            Toast.makeText(context, "Game Reset!", Toast.LENGTH_SHORT).show()
        }
    )
}


@Composable
fun GameOverSection(viewModel: GameViewModel, winner: String?) {
    val context = LocalContext.current // Extract context here

    Spacer(modifier = Modifier.height(16.dp))

    // Winner Animation or Message
    WinnerAnimation(winner = winner) // Assumes WinnerAnimation is a separate composable

    Spacer(modifier = Modifier.height(16.dp))

    // Play Again Button
    Button(
        onClick = {
            viewModel.resetGame()
            Toast.makeText(context, "Game Reset!", Toast.LENGTH_SHORT).show() // Use the extracted context
        }
    ) {
        Text("Play Again")
    }
}











@Composable
fun GameActionButtons(
    onSaveAndExit: () -> Unit,
    onResetGame: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onSaveAndExit,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF03DAC5), // Teal color
                contentColor = Color.Black          // Black text color
            ),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text("Save and Exit", fontSize = 18.sp)
        }

        Button(
            onClick = onResetGame,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EA), // Purple background
                contentColor = Color.White          // White text color
            ),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text("Reset Game", fontSize = 18.sp)
        }
    }
}

var showDialog = mutableStateOf<Boolean>(true)

@Composable
fun SavedGameDialog(onResumeGame: () -> Unit, onStartNewGame: () -> Unit) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false // Dismiss the dialog when touched outside
            },
            title = {
                Text(text = "Saved Game Found")
            },
            text = {
                Text("A previously saved game was found. Would you like to resume it or start a new game?")
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false // Dismiss the dialog
                    onResumeGame()           // Trigger resume game action
                }) {
                    Text("Resume Game")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog.value = false // Dismiss the dialog
                    onStartNewGame()         // Trigger start new game action
                }) {
                    Text("Start New Game")
                }
            }
        )
    }

}

@Composable
fun WinnerAnimation(winner: String?) {
    val scale by animateFloatAsState(
        targetValue = if (winner != null) 1.5f else 1f,
        animationSpec = tween(durationMillis = 1000)
    )

    Text(
        text = "Winner: ${winner}!",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .scale(scale)
            .padding(8.dp)
    )
}

@Composable
fun ScoreBoard(viewModel: GameViewModel) {
    Column {
        Text(text = "Scoreboard", fontSize = 24.sp, modifier = Modifier.padding(8.dp))

        viewModel.players.forEach { player ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = player.color.toString(),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Text(
                    text = "Turns: ${player.turnsTaken}",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Text(
                    text = "Score: ${player.score}",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}
