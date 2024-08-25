package com.example.simplifiedludogame

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val gameManager = GameManager()

    val currentPlayer: LiveData<Player> = gameManager.currentPlayer
    val diceRoll: LiveData<Int> = gameManager.diceRoll
    val isGameOver: LiveData<Boolean> = gameManager.isGameOver
    val playerPieces: Map<Player, List<Int>> get() = gameManager.playerPieces

    val playerScores: Map<Player, Int> get() = gameManager.playerScores

    fun rollDice() {
        gameManager.rollDice()
    }

    fun resetGame() {
        gameManager.resetGame()
    }
}
