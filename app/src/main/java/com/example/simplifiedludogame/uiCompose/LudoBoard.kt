package com.example.simplifiedludogame.uiCompose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.simplifiedludogame.GameViewModel
import com.example.simplifiedludogame.Player

@Composable
fun LudoBoard(gameViewModel: GameViewModel ) {
    val currentPlayer by gameViewModel.currentPlayer.observeAsState(Player.RED)
    val diceRoll by gameViewModel.diceRoll.observeAsState(1)
    val isGameOver by gameViewModel.isGameOver.observeAsState(false)
    val playerPieces = gameViewModel.playerPieces

    val cellSize = 30.dp

    Column {
        // Display current player
        Text("Current Player: ${currentPlayer.name}", Modifier.padding(16.dp))

        // Display dice roll
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Dice Roll: $diceRoll", Modifier.padding(16.dp))

            // Show a colored circle representing the dice roll
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(playerColor(currentPlayer), CircleShape)
            )
        }

        // Button to roll dice
        Button(onClick = { gameViewModel.rollDice() }, Modifier.padding(8.dp)) {
            Text("Roll Dice")
        }

        // Handle game over
        if (isGameOver) {
            Text(
                "Game Over! Winner: ${gameViewModel.playerScores.maxByOrNull { it.value }?.key?.name}",
                Modifier.padding(16.dp)
            )
        }

        // Display the Ludo board with paths and home areas
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Row {
                    HomeArea(Color.Red, cellSize)
                    LudoPathVerticalWhite(cellSize = cellSize)
                    LudoPathVerticalMiddleGreen(cellSize = cellSize)
                    LudoPathVerticalSecondTop(pathColor = Color.Green, cellSize = cellSize)
                    HomeArea(Color.Green, cellSize)
                }
                Row {
                    LudoColumnLeft(cellSize = cellSize)
                    CenterSquareWithPaths(cellSize)
                    LudoColumnRight(cellSize = cellSize)
                }
                Row {
                    HomeArea(Color.Blue, cellSize)
                    LudoPathVerticalSecondBottom(pathColor = Color.Blue, cellSize = cellSize)
                    LudoPathVerticalMiddleBlue(cellSize = cellSize)
                    LudoPathVerticalWhite(cellSize = cellSize)
                    HomeArea(Color.Yellow, cellSize)
                }
            }

            playerPieces.forEach { (player, pieces) ->
                pieces.forEach { position ->
                    val (x, y) = getCellCoordinates(player, position, cellSize)
                    Box(
                        modifier = Modifier
                            .size(cellSize - 10.dp)
                            .offset(x, y)
                            .background(playerColor(player), CircleShape)
                    )
                }
            }

        }
    }
}

@Composable
fun getCellCoordinates(player: Player, position: Int, cellSize: Dp): Pair<Dp, Dp> {
    return when (player) {
        Player.RED -> {
            when (position) {
                in 0..5 -> Pair((1 + position).times( cellSize), 6 * cellSize) // Horizontal move from (6, 1) to (6, 6)
                in 6..11 -> Pair(6 * cellSize, (6 - (position - 6)) * cellSize) // Vertical move up from (6, 6) to (0, 6)
                in 12..17 -> Pair((6 + (position - 12)) * cellSize, 0 * cellSize) // Horizontal move from (0, 6) to (0, 11)
                else -> Pair(0.dp, 0.dp)  // Default
            }
        }
        Player.GREEN -> {
            when (position) {
                in 0..5 -> Pair(8 * cellSize, (1 + position) * cellSize) // Vertical move down from (1, 8) to (6, 8)
                in 6..11 -> Pair((8 + (position - 6)) * cellSize, 6 * cellSize) // Horizontal move from (6, 8) to (6, 11)
                in 12..17 -> Pair(14 * cellSize, (6 + (position - 12)) * cellSize) // Vertical move from (6, 11) to (11, 11)
                else -> Pair(0.dp, 0.dp)  // Default
            }
        }
        Player.BLUE -> {
            when (position) {
                in 0..5 -> Pair(6 * cellSize, (13 - position) * cellSize) // Vertical move up from (13, 6) to (8, 6)
                in 6..11 -> Pair((6 - (position - 6)) * cellSize, 8 * cellSize) // Horizontal move left from (8, 6) to (8, 1)
                in 12..17 -> Pair(0 * cellSize, (8 - (position - 12)) * cellSize) // Vertical move from (8, 1) to (3, 1)
                else -> Pair(0.dp, 0.dp)  // Default
            }
        }
        Player.YELLOW -> {
            when (position) {
                in 0..5 -> Pair((13 - position) * cellSize, 8 * cellSize) // Horizontal move left from (8, 13) to (8, 8)
                in 6..11 -> Pair(8 * cellSize, (8 - (position - 6)) * cellSize) // Vertical move up from (8, 8) to (3, 8)
                in 12..17 -> Pair((8 - (position - 12)) * cellSize, 0 * cellSize) // Horizontal move left from (3, 8) to (3, 3)
                else -> Pair(0.dp, 0.dp)  // Default
            }
        }
        else -> Pair(0.dp, 0.dp)  // Default return for non-matching cases
    }
}


@Composable
fun LudoColumnLeft(cellSize: Dp) {
    Column {
        LudoPathHorizontalSecondLeftRed(cellSize)
        LudoPathHorizontalMiddleRed(cellSize)
        LudoPathHorizontalWhite(cellSize)
    }
}

@Composable
fun LudoColumnRight(cellSize: Dp) {
    Column {
        LudoPathHorizontalWhite(cellSize)
        LudoPathHorizontalMiddleYellow(cellSize)
        LudoPathHorizontalSecondRightYellow(cellSize)
    }
}

@Composable
fun LudoPathVerticalWhite(cellSize: Dp) {
    Column {
        repeat(6) {
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(Color.White)
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathVerticalMiddleGreen(cellSize: Dp) {
    Column {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(
                        when (index) {
                            1, 2, 3, 4, 5 -> Color.Green
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathVerticalMiddleBlue(cellSize: Dp) {
    Column {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(
                        when (index) {
                            0, 1, 2, 3, 4 -> Color.Blue
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathVerticalSecondTop(pathColor: Color, cellSize: Dp) {
    Column {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(
                        when (index) {
                            1 -> pathColor
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathVerticalSecondBottom(pathColor: Color, cellSize: Dp) {
    Column {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(
                        when (index) {
                            4 -> pathColor
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathHorizontalWhite(cellSize: Dp) {
    Row {
        repeat(6) {
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(Color.White)
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathHorizontalMiddleRed(cellSize: Dp) {
    Row {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(
                        when (index) {
                            1, 2, 3, 4, 5 -> Color.Red
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathHorizontalSecondLeftRed(cellSize: Dp) {
    Row {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(
                        when (index) {
                            1 -> Color.Red
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathHorizontalMiddleYellow(cellSize: Dp) {
    Row {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(
                        when (index) {
                            0, 1, 2, 3, 4 -> Color.Yellow
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun LudoPathHorizontalSecondRightYellow(cellSize: Dp) {
    Row {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(cellSize)
                    .background(
                        when (index) {
                            4 -> Color.Yellow
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color.Black)
            )
        }
    }
}

@Composable
fun CenterSquareWithPaths(cellSize: Dp) {
    Box(
        modifier = Modifier
            .size(cellSize * 3)
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sizeInPx = cellSize.toPx() * 3
            val halfSizeInPx = sizeInPx / 2

            // Top Triangle (Green)
            drawPath(
                path = Path().apply {
                    moveTo(0f, 0f) // Top-left
                    lineTo(sizeInPx, 0f) // Top-right
                    lineTo(halfSizeInPx, halfSizeInPx) // Center
                    close()
                },
                color = Color.Green
            )

            // Right Triangle (Yellow)
            drawPath(
                path = Path().apply {
                    moveTo(sizeInPx, 0f) // Top-right
                    lineTo(sizeInPx, sizeInPx) // Bottom-right
                    lineTo(halfSizeInPx, halfSizeInPx) // Center
                    close()
                },
                color = Color.Yellow
            )

            // Bottom Triangle (Blue)
            drawPath(
                path = Path().apply {
                    moveTo(sizeInPx, sizeInPx) // Bottom-right
                    lineTo(0f, sizeInPx) // Bottom-left
                    lineTo(halfSizeInPx, halfSizeInPx) // Center
                    close()
                },
                color = Color.Blue
            )

            // Left Triangle (Red)
            drawPath(
                path = Path().apply {
                    moveTo(0f, sizeInPx) // Bottom-left
                    lineTo(0f, 0f) // Top-left
                    lineTo(halfSizeInPx, halfSizeInPx) // Center
                    close()
                },
                color = Color.Red
            )
        }
    }
}

@Composable
fun getCellColor(cellIndex: Int): Color {
    val safeCells = setOf(1, 9, 14, 22, 27, 35, 40, 48) // Define safe cells

    return when (cellIndex) {
        in safeCells -> Color.LightGray
        else -> Color.White
    }
}

fun playerColor(player: Player): Color {
    return when (player) {
        Player.RED -> Color.Red
        Player.GREEN -> Color.Green
        Player.BLUE -> Color.Blue
        Player.YELLOW -> Color.Yellow
    }
}

@Composable
fun HomeArea(color: Color, cellSize: Dp) {
    Box(
        modifier = Modifier
            .size(cellSize * 6)
            .background(color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                PlayerPiece(color, cellSize)
                PlayerPiece(color, cellSize)
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                PlayerPiece(color, cellSize)
                PlayerPiece(color, cellSize)
            }
        }
    }
}

@Composable
fun PlayerPiece(color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color.White, CircleShape)
            .padding(4.dp)
            .background(color, CircleShape)
    )
}

