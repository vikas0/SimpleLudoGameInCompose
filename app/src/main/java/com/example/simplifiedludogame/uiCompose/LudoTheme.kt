package com.example.simplifiedludogame.uiCompose


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your custom colors for the theme
private val LudoColorScheme = lightColorScheme(
    primary = Color.Red,
    secondary = Color.Green,
    background = Color(0xFFF0EAD6),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    // Define other colors as needed
)

// Optionally define typography if you have text elements in your game
private val LudoTypography = Typography(
    // Define text styles as needed
)

@Composable
fun LudoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LudoColorScheme,
        typography = LudoTypography,
        content = content
    )
}
