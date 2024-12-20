package com.example.simplifiedludogame.uiCompose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simplifiedludogame.GameViewModel

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    onShowTokens: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Main Menu", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Display token balance
        Text("Your Tokens: ${viewModel.tokens.value}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))


        // Navigation buttons
        Button(onClick = onStartGame) {
            Text("Start Game")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onShowTokens) {
            Text("Manage Tokens")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onSignOut) {
            Text("Sign Out")
        }
    }
}


@Composable
fun CollectTokenDialog(
    onCollect: (Int) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Collect Tokens") },
        text = { Text("You can collect 50 tokens. Would you like to proceed?") },
        confirmButton = {
            Button(onClick = { onCollect(50) }) { // Simulate collecting 50 tokens
                Text("Collect")
            }
        },
        dismissButton = {
            Button(onClick = { onCancel() }) {
                Text("Cancel")
            }
        }
    )
}
