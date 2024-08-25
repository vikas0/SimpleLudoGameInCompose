package com.example.simplifiedludogame.uiCompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.simplifiedludogame.GameViewModel

@Preview(showBackground = true)
@Composable
fun LudoBoardPreview() {
    LudoTheme {
        LudoBoard(gameViewModel = GameViewModel())
    }
}
