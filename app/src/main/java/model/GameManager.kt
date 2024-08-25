package com.example.simplifiedludogame

import androidx.lifecycle.MutableLiveData

class GameManager {
    private val numberOfTurns = 13
    private var currentTurn = 0

    val playerScores = mutableMapOf<Player, Int>()
    var currentPlayer = MutableLiveData<Player>()
    var diceRoll = MutableLiveData<Int>()
    var isGameOver = MutableLiveData<Boolean>()

    // Safe cells where elimination cannot happen
    private val safeCells = setOf(1, 9, 14, 22, 27, 35, 40, 48)

    // Mapping player to their pieces' positions on the board
    val playerPieces = mutableMapOf<Player, MutableList<Int>>()

    init {
        Player.values().forEach { player ->
            playerScores[player] = 0
            playerPieces[player] = mutableListOf()
        }
        currentPlayer.value = Player.RED
        isGameOver.value = false
    }

    fun rollDice() {
        if (isGameOver.value == true) return

        val roll = (1..6).random()
        diceRoll.value = roll
        // Here you would handle moving a piece based on the dice roll
        // For simplicity, let's assume each player has one piece, and the piece moves forward by the dice roll value
        movePiece(currentPlayer.value!!, 0, roll)

        currentTurn++
        if (currentTurn >= numberOfTurns * Player.values().size) {
            isGameOver.value = true
        } else {
            currentPlayer.value = getNextPlayer(currentPlayer.value!!)
        }
    }

    private fun movePiece(player: Player, pieceIndex: Int, diceRoll: Int) {
        val pieces = playerPieces[player]!!
        if (pieces.size <= pieceIndex) {
            pieces.add(0) // Add a new piece to the board if it's the first move
        }
        val currentPos = pieces[pieceIndex]
        val newPos = currentPos + diceRoll

        if (newPos >= 50) {
            reachHome(player)
            pieces.removeAt(pieceIndex)
        } else {
            if (newPos !in safeCells) {
                eliminateOpponentPiece(newPos)
            }
            pieces[pieceIndex] = newPos
        }
    }

    private fun eliminateOpponentPiece(position: Int) {
        Player.values().forEach { player ->
            if (player != currentPlayer.value) {
                val pieces = playerPieces[player]!!
                if (pieces.contains(position)) {
                    pieces.remove(position)
                    eliminatePiece()
                }
            }
        }
    }

    private fun getNextPlayer(current: Player): Player {
        return when (current) {
            Player.RED -> Player.GREEN
            Player.GREEN -> Player.BLUE
            Player.BLUE -> Player.YELLOW
            Player.YELLOW -> Player.RED
        }
    }

    fun eliminatePiece() {
        val score = playerScores[currentPlayer.value!!] ?: 0
        playerScores[currentPlayer.value!!] = score + 10
    }

    fun reachHome(player: Player) {
        val score = playerScores[player] ?: 0
        playerScores[player] = score + 5
    }

    fun resetGame() {
        currentTurn = 0
        Player.values().forEach { player ->
            playerScores[player] = 0
            playerPieces[player]!!.clear()
        }
        currentPlayer.value = Player.RED
        isGameOver.value = false
    }
}

enum class Player {
    RED, GREEN, BLUE, YELLOW
}
