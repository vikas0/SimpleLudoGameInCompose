package com.example.simplifiedludogame.uiCompose

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
fun GameScreen(viewModel: GameViewModel, onReset: () -> Unit) {
val context = LocalContext.current
    // Observe toast message from ViewModel
    val toastMessage = viewModel.toastMessage.value

    // Display toast when message is set
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.toastMessage.value = null  // Clear the toast message after showing
        }
    }

    val isGameOver = viewModel.isGameOver.value
    val winner = viewModel.winner.value


    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Enable vertical scroll
//            .horizontalScroll(rememberScrollState()) // Enable horizontal scroll
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Display the Ludo board
            LudoBoard(viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))

            // Display the dice roll and turn
            DiceRollAndMove(viewModel = viewModel)



            Spacer(modifier = Modifier.height(16.dp))

            // Add a line separator (divider)
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.5.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Display the scoreboard
            ScoreBoard(viewModel = viewModel)

            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(
                onClick = { viewModel.resetGame() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EA),  // Purple background
                    contentColor = Color.White           // White text color
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text("Reset Game", fontSize = 18.sp)
            }

            // Display the winner animation and reset button if the game is over
            if (isGameOver) {
                Spacer(modifier = Modifier.height(16.dp))

                // Show game over message and winner animation
                WinnerAnimation(winner = winner)

                Spacer(modifier = Modifier.height(16.dp))

                // Reset button
                Button(onClick = onReset) {
                    Text("Play Again")
                }
            }

        }
    }
}

@Composable
fun WinnerAnimation(winner: Player?) {
    // Simple animation using animateFloat
    val scale by animateFloatAsState(
        targetValue = if (winner != null) 1.5f else 1f,
        animationSpec = tween(durationMillis = 1000)
    )

    // Display the winner with the scaling animation
    Text(
        text = "Winner: ${winner?.color?.name}!",
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

                viewModel.players?.forEach { player ->
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
                                .weight(1f)  // Equal weight for each column
                                .padding(start = 8.dp)
                        )
                        Text(
                            text = "Turns: ${player.turnsTaken}",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .weight(1f)  // Equal weight for each column
                                .padding(start = 8.dp)
                        )
                        Text(
                            text = "Score: ${player.score}",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .weight(1f)  // Equal weight for each column
                                .padding(start = 8.dp)
                        )
                    }
                }
            }
        }
