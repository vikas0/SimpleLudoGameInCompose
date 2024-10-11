package com.example.simplifiedludogame.uiCompose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplifiedludogame.GameViewModel
import com.example.simplifiedludogame.model.TokenColor

@Composable
fun DiceRollAndMove(viewModel: GameViewModel) {
    val context = LocalContext.current
    val currentPlayer = viewModel.currentPlayer
    val lastDiceRoll = viewModel.lastDiceRoll.value
    val diceRoll = viewModel.getCurrentDiceRoll()
    val isGameOver = viewModel.isGameOver.value
    val isDiceRollDisabled = viewModel.isDiceRollDisabled.value

    // Determine the button and text color based on the current player's color
    val playerColor = when (currentPlayer?.color) {
        TokenColor.RED -> Color.Red
        TokenColor.GREEN -> Color.Green
        TokenColor.YELLOW -> Color.Yellow
        TokenColor.BLUE -> Color.Blue
        else -> Color.Gray
    }

    Column (    modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black)
        .padding(16.dp) ) {

        Text(
            text = "Dice Roll: $diceRoll Last Dice Roll: $lastDiceRoll",
            fontSize = 24.sp,
            color = playerColor
        )
        Text(
            text = "Current Player: ${currentPlayer?.color}",
            fontSize = 18.sp,
            color = playerColor
        )

        Button(
            onClick = {     if (isGameOver) {
                viewModel.toastMessage.value =  "Game over! Please reset the game."
            } else if (isDiceRollDisabled) {
                viewModel.toastMessage.value =  "PLease move token for the last dice roller player ."
            } else {
                viewModel.rollDice()
            } },

            colors = ButtonDefaults.buttonColors(
                containerColor = playerColor,
                contentColor = Color.Black
            )
        ) {
            Text("Roll Dice")
        }


    }
}
