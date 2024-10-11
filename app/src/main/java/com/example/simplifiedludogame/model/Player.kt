package com.example.simplifiedludogame.model

data class Player(
    val color: TokenColor,         // Color of the tokens
    var turnsTaken: Int = 0,  // Track the number of turns taken by the player
    var score: Int = 0,            // Player's current score
    var hasExtraTurn: Boolean = false  // If the player rolled a 6
)

data class Piece(
    var position: Int = 0,  // Start position
    var isAtHome: Boolean = false // Track if the piece is at home
)

enum class TokenColor {
    RED, GREEN, YELLOW, BLUE
}

