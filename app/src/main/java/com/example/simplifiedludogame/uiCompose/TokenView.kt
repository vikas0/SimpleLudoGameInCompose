package com.example.simplifiedludogame.uiCompose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TokenView(color: Color) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(color = color, shape = CircleShape)
            .border(2.dp, Color.Black, CircleShape)
    )
}
