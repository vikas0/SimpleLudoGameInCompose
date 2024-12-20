package com.example.simplifiedludogame.uiCompose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplifiedludogame.GameFlowScreen
import com.example.simplifiedludogame.GameViewModel
import com.example.simplifiedludogame.viewmodel.AuthViewModel

@Composable
fun AppContent(
    authViewModel: AuthViewModel,
    gameViewModel: GameViewModel
) {
    val isSignedIn by authViewModel.isUserSignedIn.observeAsState(initial = false)
    var currentScreen by remember { mutableStateOf("auth") }

    // If sign-in state changes, move between auth and menu
    LaunchedEffect(isSignedIn) {
        currentScreen = if (isSignedIn){ "menu"} else "auth"
    }

    when (currentScreen) {
        "auth" -> AuthScreen(
            onSignInClicked = { authViewModel.onSignInClicked() }
        )
        "menu" ->
            MainMenuScreen(
                viewModel = gameViewModel,
                onStartGame = { currentScreen = "gameFlow" },
                onShowTokens = { currentScreen = "tokenManagement" },
                onSignOut = { authViewModel.signOut() }
            )

        "gameFlow" -> {
            gameViewModel.determineInitialState()
            GameFlowScreen(
                gameViewModel = gameViewModel,
                onExitToMenu = { currentScreen = "menu" },
                onManageTokens = { currentScreen = "tokenManagement" }
            )
        }
        "tokenManagement" -> TokenManagementScreen(
            gameViewModel = gameViewModel,
            onBackToMenu = { currentScreen = "menu" }
        )
    }
}

@Composable
fun TokenManagementScreen(
    gameViewModel: GameViewModel,
    onBackToMenu: () -> Unit
) {
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var showInsufficientTokensDialog by remember { mutableStateOf(false) }

    val tokens = gameViewModel.tokens.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Token Management", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Your Tokens: $tokens", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { gameViewModel.collectTokens(50) }) {
            Text("Collect 50 Tokens")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showPurchaseDialog = true }) {
            Text("Buy Tokens")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (tokens >= 20) {
                // If user has enough tokens, spend them
                gameViewModel.spendTokens(20)
            } else {
                // If user doesn't have enough tokens, show a warning dialog
                showInsufficientTokensDialog = true
            }
        }) {
            Text("Spend 20 Tokens")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBackToMenu) {
            Text("Back to Menu")
        }

        if (showPurchaseDialog) {
            PaymentDialog(
                onConfirmPurchase = { tokenCount ->
                    gameViewModel.collectTokens(tokenCount)
                    showPurchaseDialog = false
                },
                onCancel = { showPurchaseDialog = false }
            )
        }

        if (showInsufficientTokensDialog) {
            AlertDialog(
                onDismissRequest = { showInsufficientTokensDialog = false },
                title = { Text("Insufficient Tokens") },
                text = { Text("You do not have enough tokens to make this purchase.") },
                confirmButton = {
                    TextButton(onClick = { showInsufficientTokensDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun PaymentDialog(
    onConfirmPurchase: (Int) -> Unit,
    onCancel: () -> Unit
) {
    var selectedTokens by remember { mutableStateOf(100) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Buy Tokens") },
        text = {
            Column {
                Text("Select token package:")
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    listOf(100, 200, 500).forEach { tokenPackage ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTokens == tokenPackage,
                                onClick = { selectedTokens = tokenPackage }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$tokenPackage Tokens")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmPurchase(selectedTokens) }) {
                Text("Buy")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}
