package model

data class Player(
    val name: String,
    var score: Int = 0,
    val pieces: List<Piece> = List(4) { Piece() } // Four pieces per player
)

data class Piece(
    var position: Int = 0,  // Start position
    var isAtHome: Boolean = false // Track if the piece is at home
)
