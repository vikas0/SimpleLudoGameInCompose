package com.example.simplifiedludogame.model

sealed class GameState {
    object Loading : GameState()
    object PlayerSelection : GameState()
    object ShowSavedGameDialog : GameState()
    data class InGame(val isResumed: Boolean) : GameState()
}
