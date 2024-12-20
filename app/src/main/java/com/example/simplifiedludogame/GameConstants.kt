package com.example.simplifiedludogame

// Maximum number of turns allowed per player
const val MAX_TURNS = 13

// Home positions for each player
val RED_HOME_POSITIONS = listOf(Pair(1, 1), Pair(1, 4), Pair(4, 1), Pair(4, 4))
val GREEN_HOME_POSITIONS = listOf(Pair(1, 10), Pair(1, 13), Pair(4, 10), Pair(4, 13))
val YELLOW_HOME_POSITIONS = listOf(Pair(10, 10), Pair(10, 13), Pair(13, 10), Pair(13, 13))
val BLUE_HOME_POSITIONS = listOf(Pair(10, 1), Pair(10, 4), Pair(13, 1), Pair(13, 4))

// Starting positions for tokens
val RED_START_POSITION = Pair(6, 1)
val GREEN_START_POSITION = Pair(1, 8)
val YELLOW_START_POSITION = Pair(8, 13)
val BLUE_START_POSITION = Pair(13, 6)

// Safe cells
val SAFE_CELLS = listOf(
    Pair(2, 6), Pair(6, 12), Pair(12, 8), Pair(8, 2),
    RED_START_POSITION, GREEN_START_POSITION, YELLOW_START_POSITION, BLUE_START_POSITION
)

// General path for all tokens
val GENERAL_PATH = listOf(
    Pair(6, 1), Pair(6, 2), Pair(6, 3), Pair(6, 4), Pair(6, 5), Pair(5, 6), Pair(4, 6),
    Pair(3, 6), Pair(2, 6), Pair(1, 6), Pair(0, 6), Pair(0, 7), Pair(0, 8), Pair(1, 8),
    Pair(2, 8), Pair(3, 8), Pair(4, 8), Pair(5, 8), Pair(6, 9), Pair(6, 10), Pair(6, 11),
    Pair(6, 12), Pair(6, 13), Pair(6, 14), Pair(7, 14), Pair(8, 14), Pair(8, 13), Pair(8, 12),
    Pair(8, 11), Pair(8, 10), Pair(8, 9), Pair(9, 8), Pair(10, 8), Pair(11, 8), Pair(12, 8),
    Pair(13, 8), Pair(14, 8), Pair(14, 7), Pair(14, 6), Pair(13, 6), Pair(12, 6), Pair(11, 6),
    Pair(10, 6), Pair(9, 6), Pair(8, 5), Pair(8, 4), Pair(8, 3), Pair(8, 2), Pair(8, 1),
    Pair(8, 0), Pair(7, 0), Pair(6, 0)
)

// Specific paths for each color
val RED_SPECIFIC_PATH = listOf(
    Pair(7, 0), Pair(7, 1), Pair(7, 2), Pair(7, 3), Pair(7, 4), Pair(7, 5), Pair(7, 6)
)

val GREEN_SPECIFIC_PATH = listOf(
    Pair(0, 7), Pair(1, 7), Pair(2, 7), Pair(3, 7), Pair(4, 7), Pair(5, 7), Pair(6, 7)
)

val YELLOW_SPECIFIC_PATH = listOf(
    Pair(7, 14), Pair(7, 13), Pair(7, 12), Pair(7, 11), Pair(7, 10), Pair(7, 9), Pair(7, 8)
)

val BLUE_SPECIFIC_PATH = listOf(
    Pair(14, 7), Pair(13, 7), Pair(12, 7), Pair(11, 7), Pair(10, 7), Pair(9, 7), Pair(8, 7)
)
