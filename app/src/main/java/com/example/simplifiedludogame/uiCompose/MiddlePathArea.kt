package com.example.simplifiedludogame.uiCompose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MiddlePathArea(horizontal: Boolean, cellSize: Dp) {
    if (horizontal) {
        Row {
            repeat(6) {
                PathCell(cellSize)
            }
        }
    } else {
        Column {
            repeat(6) {
                PathCell(cellSize)
            }
        }
    }
}

@Composable
fun PathCell(cellSize: Dp) {
    Box(
        modifier = Modifier
            .size(cellSize)
            .background(Color.White)
            .border(1.dp, Color.Black)
    )
}
