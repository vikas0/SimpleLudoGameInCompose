package com.example.simplifiedludogame.uiCompose

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.simplifiedludogame.GameViewModel
import com.example.simplifiedludogame.model.TokenColor

@Composable
fun LudoBoard(viewModel: GameViewModel) {

    // To access context for Toast
    val context = LocalContext.current

    // State to track the toast message
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Effect to show the toast when the message is set
    LaunchedEffect(toastMessage) {

        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            toastMessage = null // Reset the message after showing the toast
        }
    }


    // Observe the token positions using LiveData
    val redTokenPositions = viewModel.redTokenPositions.observeAsState(listOf())
    val greenTokenPositions = viewModel.greenTokenPositions.observeAsState(listOf())
    val yellowTokenPositions = viewModel.yellowTokenPositions.observeAsState(listOf())
    val blueTokenPositions = viewModel.blueTokenPositions.observeAsState(listOf())

    val diceRoll = viewModel.currentDiceRoll.value
    val currentPlayer = viewModel.currentPlayer

    val safeCells = viewModel.safeCells
    val redSafeCell = viewModel.redAt0
    val greenSafeCell = viewModel.greenAt0
    val yellowSafeCell = viewModel.yellowAt0
    val blueSafeCell = viewModel.blueAt0


    // Define the size for each grid item (e.g., 40.dp per cell)
    val gridItemSize = 40.dp
    val boardHeight = gridItemSize * 15 // Height of the board (15 rows)


    // Enable scrolling both vertically and horizontally
        LazyVerticalGrid(
            columns = GridCells.Fixed(15), // 15x15 board using GridCells.Fixed
            modifier = Modifier
                .fillMaxWidth() // Take the full width of the screen
                .height(boardHeight) // Set fixed height for the grid to allow scrolling

        ) {
            items(15 * 15) { index ->
                val row = index / 15
                val column = index % 15
                val position = row to column

                // Determine the color for each cell
                val color = when {
                     position == viewModel.redAt0 -> Color.Red.copy(alpha = 0.4f) // Light red for red safe cell
                    position == viewModel.greenAt0 -> Color.Green.copy(alpha = 0.4f) // Light green for green safe cell
                    position == viewModel.yellowAt0 -> Color.Yellow.copy(alpha = 0.4f) // Light yellow for yellow safe cell
                    position == viewModel.blueAt0 -> Color.Blue.copy(alpha = 0.4f) // Light blue for blue safe cell

                    position in safeCells -> Color.LightGray // Color for general safe cells


                    position in viewModel.redSpecificPath.map { it.first } -> Color.Red
                    position in viewModel.greenSpecificPath.map { it.first } -> Color.Green
                    position in viewModel.yellowSpecificPath.map { it.first } -> Color.Yellow
                    position in viewModel.blueSpecificPath.map { it.first } -> Color.Blue
                    position in viewModel.generalPath.map { it.first } -> Color.Gray


                    else -> Color.Transparent
                }
                // Get all tokens in this position
                val tokensInCell = getTokensInCell(viewModel, position)

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color.Black) // Adding border around each cell

                        .background(color = color)
                        .clickable {

                            viewModel.onCellClick(position, tokensInCell)

                        }
                ) {
                    // Draw tokens at their current positions
                    when (position) {
                        in redTokenPositions.value!! -> TokenView(Color.Red)
                        in greenTokenPositions.value!! -> TokenView(Color.Green)
                        in yellowTokenPositions.value!! -> TokenView(Color.Yellow)
                        in blueTokenPositions.value!! -> TokenView(Color.Blue)
                    }
                }
            }
        }



}

// Helper function to get all tokens in a cell
private fun getTokensInCell(viewModel: GameViewModel, position: Pair<Int, Int>): List<TokenColor> {
    val tokensInCell = mutableListOf<TokenColor>()
    if (position in viewModel.redTokenPositions.value!!) tokensInCell.add(TokenColor.RED)
    if (position in viewModel.greenTokenPositions.value!!) tokensInCell.add(TokenColor.GREEN)
    if (position in viewModel.yellowTokenPositions.value!!) tokensInCell.add(TokenColor.YELLOW)
    if (position in viewModel.blueTokenPositions.value!!) tokensInCell.add(TokenColor.BLUE)
    return tokensInCell
}


@Composable
fun TokenStackView(tokens: List<TokenColor>) {
    Row(modifier = Modifier.padding(4.dp)) {
        tokens.forEach { tokenColor ->
            Box(
                modifier = Modifier
                    .size(12.dp) // Smaller size for multiple tokens
                    .background(color = when (tokenColor) {
                        TokenColor.RED -> Color.Red
                        TokenColor.GREEN -> Color.Green
                        TokenColor.YELLOW -> Color.Yellow
                        TokenColor.BLUE -> Color.Blue
                    })
            )
        }
    }
}