package com.example.simplifiedludogame.uiCompose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplifiedludogame.GameViewModel


@Composable
fun PlayerSelectionScreen(viewModel: GameViewModel, onPlayersSelected: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Number of Players", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                viewModel.updatePlayerCount(2)  // Save 2 players and initialize
                onPlayersSelected()  // Proceed to the game screen
            }) {
                Text("2 Players")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                viewModel.updatePlayerCount(4)  // Save 4 players and initialize
                onPlayersSelected()  // Proceed to the game screen
            }) {
                Text("4 Players")
            }
        }
    }
}

