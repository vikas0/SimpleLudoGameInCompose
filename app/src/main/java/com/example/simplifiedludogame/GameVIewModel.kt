package com.example.simplifiedludogame

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplifiedludogame.model.GameState
import com.example.simplifiedludogame.model.GameStateObject
import com.example.simplifiedludogame.model.Player
import com.example.simplifiedludogame.model.TokenColor
import com.example.simplifiedludogame.model.toMap
import com.example.simplifiedludogame.preference.PreferenceHelper

import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository,
    private val preferenceHelper: PreferenceHelper
) :ViewModel()
{

    private val _gameState = mutableStateOf<GameState>(GameState.Loading)
    val gameState: State<GameState> get() = _gameState



    public fun determineInitialState() {
        Log.d("ViewModel", "Determining initial state...")
        _gameState.value = GameState.Loading
        if (playersSelected) {
            Log.d("ViewModel", "Players are selected. Checking for saved game...")

            viewModelScope.launch {
                withTimeoutOrNull(5000) {
                    checkIfGameIsSaved()
                } ?: run {
                    Log.e("ViewModel", "Timeout while fetching saved game.")
                    _gameState.value = GameState.InGame(isResumed  = false)
                }
            }
        } else {
            Log.d("ViewModel", "No players selected. Going to PlayerSelection state.")
            _gameState.value = GameState.PlayerSelection
        }
    }

    private fun checkIfGameIsSaved() {
        repository.getSavedGame { savedState, error ->
            Log.d("ViewModel", "Saved state fetched: $savedState, error: $error")
            this.savedState = savedState
            if (error != null || savedState == null) {
                Log.d("ViewModel", "No saved state found or error occurred. Starting a new game.")
                _gameState.value = GameState.InGame(isResumed = false)
            } else {
                Log.d("ViewModel", "Saved state found. Showing dialog.")
                _gameState.value = GameState.ShowSavedGameDialog
            }
        }
    }




    fun startNewGame() {
        resetGameState()
        _gameState.value = GameState.InGame(isResumed = false)
    }

    fun resumeGame() {
        restoreGameState(savedState ?: emptyMap())
        _gameState.value = GameState.InGame(isResumed = true)
    }

    fun resetToPlayerSelection() {
        clearPlayersSelected()
        _gameState.value = GameState.PlayerSelection
    }


  var  savedState: Map<String, Any>? = null

    val playersSelected: Boolean
        get() = preferenceHelper.getPlayerCount() > 0

    fun clearPlayersSelected() {
        preferenceHelper.clearPlayersSelected()
    }

    private fun restoreGameState(savedState: Map<String, Any>) {
        // Restore red token positions
        redTokenPositions.value = (savedState["redTokenPositions"] as? List<Map<String, Any>>)?.map {
            val first = (it["first"] as? Number)?.toInt() ?: 0
            val second = (it["second"] as? Number)?.toInt() ?: 0
            Pair(first, second)
        } ?: emptyList()

        // Restore green token positions
        greenTokenPositions.value = (savedState["greenTokenPositions"] as? List<Map<String, Any>>)?.map {
            val first = (it["first"] as? Number)?.toInt() ?: 0
            val second = (it["second"] as? Number)?.toInt() ?: 0
            Pair(first, second)
        } ?: emptyList()

// Restore yellow token positions
        yellowTokenPositions.value = (savedState["yellowTokenPositions"] as? List<Map<String, Any>>)?.map {
            val first = (it["first"] as? Number)?.toInt() ?: 0
            val second = (it["second"] as? Number)?.toInt() ?: 0
            Pair(first, second)
        } ?: emptyList()

// Restore blue token positions
        blueTokenPositions.value = (savedState["blueTokenPositions"] as? List<Map<String, Any>>)?.map {
            val first = (it["first"] as? Number)?.toInt() ?: 0
            val second = (it["second"] as? Number)?.toInt() ?: 0
            Pair(first, second)
        } ?: emptyList()


        // Restore current player index
        currentPlayerIndex.value = (savedState["currentPlayerIndex"] as? Number?)?.toInt() ?: 0
        // Restore last dice roll
        lastDiceRoll.value = (savedState["lastDiceRoll"] as? Number)?.toInt() ?: 0

        // Restore current dice roll
        currentDiceRoll.value = (savedState["currentDiceRoll"] as? Number)?.toInt() ?: 0

        // Restore dice roll disabled state
        isDiceRollDisabled.value = (savedState["isDiceRollDisabled"] as? Boolean) ?: false


        // Restore game over status
        isGameOver.value = (savedState["isGameOver"] as? Boolean) ?: false

        if(isGameOver.value)
        {
            winner.value = (savedState["winner"] as? String)
        }

        // Restore players
        val playerDataList = savedState["players"] as? List<Map<String, Any>> ?: emptyList()
        _players.clear()
        _players.addAll(playerDataList.map {
            Player(
                color = TokenColor.valueOf(it["color"] as? String ?: "RED"),
                score = (it["score"] as? Long)?.toInt() ?: 0,
                turnsTaken = (it["turnsTaken"] as? Long)?.toInt() ?: 0,
                hasExtraTurn = it["hasExtraTurn"] as? Boolean ?: false
            )
        })
        // Restore turns taken
        turnsTaken.value = (savedState["turnsTaken"] as? Long)?.toInt() ?: 0}

    private fun resetGameState() {
       resetGame()
    }



        fun saveGameState() {
            val gameState = collectGameState().toMap()
            val currentStateJson = Gson().toJson(gameState)  // Convert to JSON or preferred format
            val lastSyncedState = preferenceHelper.getLastSyncedState() // Retrieve the last synced state

            // Compare the current state with the last synced state
            if (currentStateJson != lastSyncedState) {
                repository.saveGameState(gameState) { isSuccess, message->
                    if (isSuccess) {
                        preferenceHelper.saveLastSyncedState(currentStateJson) // Save the new state
                        toastMessage.value = "Game state saved successfully!"
                    } else {
                        toastMessage.value = "Failed to save game state. Error = $message"
                    }
                }
            }
        }

    private fun collectGameState(): GameStateObject {
        return GameStateObject(
            redTokenPositions = redTokenPositions.value ?: emptyList(),
            greenTokenPositions = greenTokenPositions.value ?: emptyList(),
            yellowTokenPositions = yellowTokenPositions.value ?: emptyList(),
            blueTokenPositions = blueTokenPositions.value ?: emptyList(),
            currentPlayerIndex = currentPlayerIndex.value,
            lastDiceRoll = lastDiceRoll.value,
            currentDiceRoll = currentDiceRoll.value,
            isDiceRollDisabled = isDiceRollDisabled.value,
            isGameOver = isGameOver.value,
            winner = winner?.value?:"",
            players = _players.toList(),
            turnsTaken = turnsTaken.value
        )
    }



    val redAt0 = Pair(6, 1)
    val greenAt0 = Pair(1, 8)
    val yellowAt0 = Pair(8, 13)
    val blueAt0 = Pair(13, 6)

    val safeCells = listOf<Pair<Int, Int>>( Pair(2,6),Pair(6,12), Pair(12,8), Pair(8,2),redAt0,greenAt0,
        yellowAt0,blueAt0)


    private val maxTurns = 13  // Maximum number of turns allowed per player
    private val redHomePositions = listOf(
        Pair(1, 1), Pair(1, 4), Pair(4, 1), Pair(4, 4)
    )
    private val greenHomePositions = listOf(
        Pair(1, 10), Pair(1, 13), Pair(4, 10), Pair(4, 13)
    )
    private val yellowHomePositions = listOf(
        Pair(10, 10), Pair(10, 13), Pair(13, 10), Pair(13, 13)
    )
    private val blueHomePositions = listOf(
       Pair(10, 1), Pair(10, 4), Pair(13, 1), Pair(13, 4)
    )
    // Token positions for each player
    val redTokenPositions = MutableLiveData<List<Pair<Int, Int>>>(redHomePositions)
    val greenTokenPositions = MutableLiveData<List<Pair<Int, Int>>>(greenHomePositions)
    val yellowTokenPositions = MutableLiveData<List<Pair<Int, Int>>>(yellowHomePositions)
    val blueTokenPositions = MutableLiveData<List<Pair<Int, Int>>>(blueHomePositions)

    val generalPath = listOf(
        Pair(6 to 1, 6 to 2), Pair(6 to 2, 6 to 3), Pair(6 to 3, 6 to 4), Pair(6 to 4, 6 to 5),
        Pair(6 to 5, 5 to 6), Pair(5 to 6, 4 to 6), Pair(4 to 6, 3 to 6), Pair(3 to 6, 2 to 6),
        Pair(2 to 6, 1 to 6), Pair(1 to 6, 0 to 6), Pair(0 to 6, 0 to 7), Pair(0 to 7, 0 to 8),
        Pair(0 to 8, 1 to 8), Pair(1 to 8, 2 to 8), Pair(2 to 8, 3 to 8), Pair(3 to 8, 4 to 8),
        Pair(4 to 8, 5 to 8), Pair(5 to 8, 6 to 9), Pair(6 to 9, 6 to 10), Pair(6 to 10, 6 to 11),
        Pair(6 to 11, 6 to 12), Pair(6 to 12, 6 to 13), Pair(6 to 13, 6 to 14), Pair(6 to 14, 7 to 14),
        Pair(7 to 14, 8 to 14), Pair(8 to 14, 8 to 13), Pair(8 to 13, 8 to 12), Pair(8 to 12, 8 to 11),
        Pair(8 to 11, 8 to 10), Pair(8 to 10, 8 to 9), Pair(8 to 9, 9 to 8), Pair(9 to 8, 10 to 8),
        Pair(10 to 8, 11 to 8), Pair(11 to 8, 12 to 8), Pair(12 to 8, 13 to 8), Pair(13 to 8, 14 to 8),
        Pair(14 to 8, 14 to 7), Pair(14 to 7, 14 to 6), Pair(14 to 6, 13 to 6),Pair(13 to 6, 12 to 6),
        Pair(12 to 6, 11 to 6), Pair(11 to 6, 10 to 6),Pair(10 to 6, 9 to 6),Pair(9 to 6, 8 to 5),
        Pair(8 to 5, 8 to 4), Pair(8 to 4, 8 to 3), Pair(8 to 3, 8 to 2), Pair(8 to 2, 8 to 1),
        Pair(8 to 1, 8 to 0), Pair(8 to 0, 7 to 0), Pair(7 to 0, 6 to 0), Pair(6 to 0, 6 to 1)
    )

    // Home positions for tokens (4 tokens per home area, equally spaced in the 6x6 area)


    val redSpecificPath = listOf(
        Pair(7 to 0, 7 to 1), Pair(7 to 1, 7 to 2), Pair(7 to 2, 7 to 3), Pair(7 to 3, 7 to 4),
        Pair(7 to 4, 7 to 5), Pair(7 to 5, 7 to 6)  // Final destination for Red
    )

    val greenSpecificPath = listOf(
        Pair(0 to 7, 1 to 7), Pair(1 to 7, 2 to 7), Pair(2 to 7, 3 to 7), Pair(3 to 7, 4 to 7),
        Pair(4 to 7, 5 to 7), Pair(5 to 7, 6 to 7)  // Final destination for Green
    )

    val yellowSpecificPath = listOf(
        Pair(7 to 14, 7 to 13), Pair(7 to 13, 7 to 12), Pair(7 to 12, 7 to 11), Pair(7 to 11, 7 to 10),
        Pair(7 to 10, 7 to 9), Pair(7 to 9, 7 to 8)  // Final destination for Yellow
    )

    val blueSpecificPath = listOf(
        Pair(14 to 7, 13 to 7), Pair(13 to 7, 12 to 7), Pair(12 to 7, 11 to 7), Pair(11 to 7, 10 to 7),
        Pair(10 to 7, 9 to 7), Pair(9 to 7, 8 to 7)  // Final destination for Blue
    )

    // Track the number of turns each player has taken
    private val turnsTaken = mutableStateOf(0)


    // Disable roll dice button after the game ends
    var isGameOver = mutableStateOf(false)
    var winner = mutableStateOf<String?>(null)  // Track the winner
    // Track the toast message
    var toastMessage = mutableStateOf<String?>(null)

        // Use mutableStateListOf to track changes in the player list
        private val _players = mutableStateListOf<Player>()
        val players: List<Player> = _players

        // Other game state variables can also use MutableState
        var currentPlayerIndex = mutableStateOf(0)  // Track the current player
    var lastDiceRoll = mutableStateOf(0)
        var currentDiceRoll = mutableStateOf(0)     // Track the current dice roll


    var tokenMoved = mutableStateOf(false)

    // Track if the dice roll button is disabled after rolling
    var isDiceRollDisabled = mutableStateOf(false)

    // Getter for the current player
    val currentPlayer: Player
        get() = _players[currentPlayerIndex.value]

    // Getter for the current dice roll
    fun getCurrentDiceRoll(): Int {
        return currentDiceRoll.value
    }

        // Initialize players for 2-player or 4-player game
        fun initializePlayers(playerCount: Int) {
            val playerList = if (playerCount == 2) {
                listOf(Player(TokenColor.RED), Player(TokenColor.YELLOW))
            } else {
                listOf(Player(TokenColor.RED), Player(TokenColor.GREEN), Player(TokenColor.YELLOW), Player(TokenColor.BLUE))
            }
            _players.clear()
            _players.addAll(playerList)
            currentPlayerIndex.value = 0  // Start with the first player
        }

    init {
        // Get player count from PreferenceHelper (default to 2 players if not set)
        val playerCount = preferenceHelper.getPlayerCount()
        initializePlayers(playerCount)
//        initializeTokensForTesting()
    }


    // Function to update the player count using PreferenceHelper
    fun updatePlayerCount(playerCount: Int) {
        preferenceHelper.savePlayerCount(playerCount)
        initializePlayers(playerCount) // Re-initialize players
    }

        // Roll the dice
        @SuppressLint("SuspiciousIndentation")
        fun rollDice() {
            // Check if the game is already over
            if (isGameOver.value) return

            currentDiceRoll.value = (1..6).random()

            // Get the current player
            val currentPlayer = _players[currentPlayerIndex.value]
            if(!_players[currentPlayerIndex.value].hasExtraTurn )
                currentPlayer.turnsTaken++


            // Add the dice roll value directly to the current player's score
            updateScore(currentPlayer.color, currentDiceRoll.value!!)

            // Disable dice roll after rolling
            isDiceRollDisabled.value = true

            // Check if the player can move based on the dice roll and token positions
            if (!canPlayerMove(currentPlayer, currentDiceRoll.value!!)) {
                // If the player cannot move, immediately go to the next player

                completeTurn()

            }

      tokenMoved.value = false  // Playeroke still needs to move the token

                // If the dice roll is 6, give the player an extra turn
                if (currentDiceRoll.value == 6) {
                    _players[currentPlayerIndex.value].hasExtraTurn = true
                } else {
                    _players[currentPlayerIndex.value].hasExtraTurn = false
                }
//            }
        }





    // Move to the next player after their turn
    fun moveToNextPlayer() {
        currentPlayerIndex.value = (currentPlayerIndex.value + 1) % _players.size
        // Reset the dice roll icollectGameStatef moving to the next player
        lastDiceRoll.value = currentDiceRoll.value
        currentDiceRoll.value = 0 // Reset dice roll after the player moves to the next
        isDiceRollDisabled.value = false
    }

    // Move the token and handle scoring and elimination logic

    fun isTokenMovable(tokenColor: TokenColor, position: Pair<Int, Int>): Boolean {
        return when (tokenColor) {
            TokenColor.RED -> redTokenPositions.value?.contains(position) == true
            TokenColor.GREEN -> greenTokenPositions.value?.contains(position) == true
            TokenColor.YELLOW -> yellowTokenPositions.value?.contains(position) == true
            TokenColor.BLUE -> blueTokenPositions.value?.contains(position) == true
            else -> false
        }
    }

    // Calculate final position of the token based on dice roll
    fun calculateFinalPosition(color: TokenColor, currentPosition: Pair<Int, Int>, diceRoll: Int): Pair<Int, Int> {
        var position = currentPosition
        for (i in 1..diceRoll) {
            position = moveOneStep(color, position)
        }
        return position
    }

    private fun moveOneStep(color: TokenColor, currentPosition: Pair<Int, Int>): Pair<Int, Int> {
        return when (color) {
            TokenColor.RED -> {
                // Find the pair where the first value matches the currentPosition
                redSpecificPath.find { it.first == currentPosition }?.second ?: moveInGeneralPath(currentPosition)
            }
            TokenColor.GREEN -> {
                greenSpecificPath.find { it.first == currentPosition }?.second ?: moveInGeneralPath(currentPosition)
            }
            TokenColor.YELLOW -> {
                yellowSpecificPath.find { it.first == currentPosition }?.second ?: moveInGeneralPath(currentPosition)
            }
            TokenColor.BLUE -> {
                blueSpecificPath.find { it.first == currentPosition }?.second ?: moveInGeneralPath(currentPosition)
            }
            else -> currentPosition  // Return the current position if no match is found
        }
    }

    private fun moveInGeneralPath(currentPosition: Pair<Int, Int>): Pair<Int, Int> {
        // Find the pair where the first value matches the currentPosition
        return generalPath.find { it.first == currentPosition }?.second ?: currentPosition
    }

  // Update token position based on the player's color
    private fun updateTokenPosition(color: TokenColor, oldPosition: Pair<Int, Int>, newPosition: Pair<Int, Int>) {
        when (color) {
            TokenColor.RED -> redTokenPositions.value = redTokenPositions.value!!.map {
                if (it == oldPosition) newPosition else it
            }
            TokenColor.GREEN -> greenTokenPositions.value = greenTokenPositions.value!!.map {
                if (it == oldPosition) newPosition else it
            }
            TokenColor.YELLOW -> yellowTokenPositions.value = yellowTokenPositions.value!!.map {
                if (it == oldPosition) newPosition else it
            }
            TokenColor.BLUE -> blueTokenPositions.value = blueTokenPositions.value!!.map {
                if (it == oldPosition) newPosition else it
            }
        }
    }

    // Check if the token is in the game area
    private fun isGameArea(position: Pair<Int, Int>, color: TokenColor): Boolean {
        return when (color) {
            TokenColor.RED -> position in redSpecificPath.map { it.first }
            TokenColor.GREEN -> position in greenSpecificPath.map { it.first }
            TokenColor.YELLOW -> position in yellowSpecificPath.map { it.first }
            TokenColor.BLUE -> position in blueSpecificPath.map { it.first }
        }
    }

    // Get opponent tokens
    private fun getOpponentTokens(color: TokenColor): List<Pair<Int, Int>> {
        return when (color) {
            TokenColor.RED -> greenTokenPositions.value!! + yellowTokenPositions.value!! + blueTokenPositions.value!!
            TokenColor.GREEN -> redTokenPositions.value!! + yellowTokenPositions.value!! + blueTokenPositions.value!!
            TokenColor.YELLOW -> redTokenPositions.value!! + greenTokenPositions.value!! + blueTokenPositions.value!!
            TokenColor.BLUE -> redTokenPositions.value!! + greenTokenPositions.value!! + yellowTokenPositions.value!!
        }
    }

    // Function to check if a token can be eliminated
    private fun canBeEliminated(position: Pair<Int, Int>, tokenColor: TokenColor): Boolean {
        // Check if the position is a general safe cell
       return !(position in safeCells)
    }


    private fun eliminateOpponentToken(position: Pair<Int, Int>) {
        when {
            position in redTokenPositions.value!! -> {
                redTokenPositions.value = sendTokenBackToHome(TokenColor.RED, position)
            }
            position in greenTokenPositions.value!! -> {
                greenTokenPositions.value = sendTokenBackToHome(TokenColor.GREEN, position)
            }
            position in yellowTokenPositions.value!! -> {
                yellowTokenPositions.value = sendTokenBackToHome(TokenColor.YELLOW, position)
            }
            position in blueTokenPositions.value!! -> {
                blueTokenPositions.value = sendTokenBackToHome(TokenColor.BLUE, position)
            }
        }
    }


    // Update score
    fun updateScore(tokenColor: TokenColor, points: Int) {
        // Find the player with the specified tokenColor
        val playerIndex = _players.indexOfFirst { it.color == tokenColor }

        // If a player is found, update their score
        if (playerIndex != -1) {
            val player = _players[playerIndex]
            _players[playerIndex] = player.copy(score = player.score + points)
        }
    }

    // Starting positions for tokens after rolling a six
    private val redStartPosition = Pair(6, 1)
    private val greenStartPosition = Pair(1, 8)
    private val yellowStartPosition = Pair(8, 13)
    private val blueStartPosition = Pair(13, 6)



    fun getCurrentPlayerTokenPositions(currentPlayerColor: TokenColor): List<Pair<Int, Int>> {
        return when (currentPlayerColor) {
            TokenColor.RED -> redTokenPositions.value!!
            TokenColor.GREEN -> greenTokenPositions.value!!
            TokenColor.YELLOW -> yellowTokenPositions.value!!
            TokenColor.BLUE -> blueTokenPositions.value!!
        }
    }

    fun resetGame() {
        // Reset token positions
        redTokenPositions.value = redHomePositions
        greenTokenPositions.value = greenHomePositions
        yellowTokenPositions.value = yellowHomePositions
        blueTokenPositions.value = blueHomePositions

        // Reset players' states
        _players.forEach { player ->
            player.score = 0
            player.hasExtraTurn = false
            player.turnsTaken = 0
        }

        // Reset game variables
        currentPlayerIndex.value = 0
        lastDiceRoll.value = 0
        currentDiceRoll.value = 0
        isDiceRollDisabled.value = false
        isGameOver.value = false
        turnsTaken.value = 0

        // Clear any winner
        winner.value = null

        // Optionally, log or display a message
        toastMessage.value = "Game has been reset. Ready to play!"
    }


    fun isTokenMatchingPlayer(tokenColor: TokenColor, position: Pair<Int, Int>): Boolean {
    return when (tokenColor) {
            TokenColor.RED -> position in redTokenPositions.value!!
            TokenColor.GREEN -> position in greenTokenPositions.value!!
            TokenColor.YELLOW -> position in yellowTokenPositions.value!!
            TokenColor.BLUE -> position in blueTokenPositions.value!!
        }
    }

    fun areAllTokensInHome(tokenColor: TokenColor): Boolean {
        return when (tokenColor) {
            TokenColor.RED -> redTokenPositions.value!!.all { it in redHomePositions }
            TokenColor.GREEN -> greenTokenPositions.value!!.all { it in greenHomePositions }
            TokenColor.YELLOW -> yellowTokenPositions.value!!.all { it in yellowHomePositions }
            TokenColor.BLUE -> blueTokenPositions.value!!.all { it in blueHomePositions }
        }
    }

fun isTokenInHome(tokenColor: TokenColor, position: Pair<Int, Int>): Boolean {
        return when (tokenColor) {
            TokenColor.RED -> position in redHomePositions
            TokenColor.GREEN -> position in greenHomePositions
            TokenColor.YELLOW -> position in yellowHomePositions
            TokenColor.BLUE -> position in blueHomePositions
        }
    }

    private fun moveTokenToNewPosition(
        tokenPositions: MutableLiveData<List<Pair<Int, Int>>>,
        currentPosition: Pair<Int, Int>,
        newPosition: Pair<Int, Int>
    ) {
        val currentTokens = tokenPositions.value!!.toMutableList()
        val index = currentTokens.indexOfFirst { it == currentPosition }

        // Only update the token that matches the current position
        if (index != -1) {
            currentTokens[index] = newPosition
            tokenPositions.value = currentTokens  // Update the token positions list
        }
    }

    fun moveFromHome(tokenColor: TokenColor, selectedTokenPosition: Pair<Int, Int>) {
        val startPosition = when (tokenColor) {
            TokenColor.RED -> redStartPosition
            TokenColor.GREEN -> greenStartPosition
            TokenColor.YELLOW -> yellowStartPosition
            TokenColor.BLUE -> blueStartPosition
        }

        // Move only the first token found at the selected home position to the start position
        when (tokenColor) {
            TokenColor.RED -> moveTokenToNewPosition(redTokenPositions, selectedTokenPosition, startPosition)
            TokenColor.GREEN -> moveTokenToNewPosition(greenTokenPositions, selectedTokenPosition, startPosition)
            TokenColor.YELLOW -> moveTokenToNewPosition(yellowTokenPositions, selectedTokenPosition, startPosition)
            TokenColor.BLUE -> moveTokenToNewPosition(blueTokenPositions, selectedTokenPosition, startPosition)
        }

        setTokenMoved()
    }

    fun moveToken(tokenColor: TokenColor, currentPosition: Pair<Int, Int>, diceRoll: Int) {

        // If token is in the final position, it cannot move further
        if (isInFinalPosition(tokenColor, currentPosition)) {
            toastMessage.value = "Token has reached its final position!"
            return
        }

        val newPosition = calculateFinalPosition(tokenColor, currentPosition, diceRoll)

        // Handle scoring, elimination, or other game logic here
        if (isGameArea(newPosition, tokenColor)) {
            updateScore(tokenColor, 5)
        }

        // Check if the new position has an opponent token
        val opponentTokens = getOpponentTokens(tokenColor)
        if (newPosition in opponentTokens) {
           getTokenColorAtPosition(newPosition) ?.let { opponentColor ->
               // Assuming a function to get token color at a position
               if (canBeEliminated(newPosition, opponentColor)) {
                   eliminateOpponentToken(newPosition)
                   updateScore(tokenColor, 10)
               }
           }
        }

        // Directly move the token found at the current position
        when (tokenColor) {
            TokenColor.RED -> moveTokenToNewPosition(redTokenPositions, currentPosition, newPosition)
            TokenColor.GREEN -> moveTokenToNewPosition(greenTokenPositions, currentPosition, newPosition)
            TokenColor.YELLOW -> moveTokenToNewPosition(yellowTokenPositions, currentPosition, newPosition)
            TokenColor.BLUE -> moveTokenToNewPosition(blueTokenPositions, currentPosition, newPosition)
        }

        setTokenMoved()

        // Check if all tokens of the player are in the final position
        val allTokensInFinalPosition = when (tokenColor) {
            TokenColor.RED -> redTokenPositions.value!!.all { isInFinalPosition(TokenColor.RED, it) }
            TokenColor.GREEN -> greenTokenPositions.value!!.all { isInFinalPosition(TokenColor.GREEN, it) }
            TokenColor.YELLOW -> yellowTokenPositions.value!!.all { isInFinalPosition(TokenColor.YELLOW, it) }
            TokenColor.BLUE -> blueTokenPositions.value!!.all { isInFinalPosition(TokenColor.BLUE, it) }
        }

        // Declare winner if all tokens are in the final position
        if (allTokensInFinalPosition) {
            isGameOver.value = true
            winner.value = _players.first { it.color == tokenColor }?.color?.name
            toastMessage.value = "${tokenColor.name} wins!"
        }


    }

    // Helper function to get the token color at a specific position
    private fun getTokenColorAtPosition(position: Pair<Int, Int>): TokenColor? {
        return when {
            position in redTokenPositions.value!! -> TokenColor.RED
            position in greenTokenPositions.value!! -> TokenColor.GREEN
            position in yellowTokenPositions.value!! -> TokenColor.YELLOW
            position in blueTokenPositions.value!! -> TokenColor.BLUE
            else -> null
        }
    }

    private fun setTokenMoved() {
        // Mark the token as moved
        isDiceRollDisabled.value = false
        tokenMoved.value = true



        if (!_players[currentPlayerIndex.value].hasExtraTurn) {

            completeTurn()
        } else {
            // If they have an extra turn, reset the dice roll for another turn
            currentDiceRoll.value = 0

        }
    }

    // Handle turn completion
    private fun completeTurn() {
        // Increment the turn count
        turnsTaken.value++

        // Check if all turns are complete
        if (turnsTaken.value >= maxTurns * _players.size) {
            endGame()
        } else {
            moveToNextPlayer()
        }
    }

    private fun endGame() {
        isGameOver.value = true
        val winnerPLayer = _players.maxByOrNull { it.score }
        winner.value = winnerPLayer?.color?.name
        toastMessage.value = "Game Over! Winner: ${winner} with ${winnerPLayer?.score} points!"
    }

    private fun canPlayerMove(player: Player, diceRoll: Int): Boolean {
        // Get the player's token positions
        val tokenPositions = when (player.color) {
            TokenColor.RED -> redTokenPositions.value!!
            TokenColor.GREEN -> greenTokenPositions.value!!
            TokenColor.YELLOW -> yellowTokenPositions.value!!
            TokenColor.BLUE -> blueTokenPositions.value!!
        }

        // Check if all tokens are in home and dice is less than 6
        if (tokenPositions.all { isInHomeArea(player.color, it) } && diceRoll < 6) {
            toastMessage.value = "All tokens are in the home. You need a 6 to move. Passing to the next player."
            return false
        }

        // Check if any token can move (not in final position and not in home)
        return tokenPositions.any { position ->
           !isInFinalPosition(player.color, position)
        }
    }

    private fun isInFinalPosition(tokenColor: TokenColor, position: Pair<Int, Int>): Boolean {
        return when (tokenColor) {
            TokenColor.RED -> position == redSpecificPath.last().second
            TokenColor.GREEN -> position == greenSpecificPath.last().second
            TokenColor.YELLOW -> position == yellowSpecificPath.last().second
            TokenColor.BLUE -> position == blueSpecificPath.last().second
        }
    }

    // Check if a token is in its home area
    private fun isInHomeArea(tokenColor: TokenColor, position: Pair<Int, Int>): Boolean {
        return when (tokenColor) {
            TokenColor.RED -> redHomePositions.contains(position)
            TokenColor.GREEN -> greenHomePositions.contains(position)
            TokenColor.YELLOW -> yellowHomePositions.contains(position)
            TokenColor.BLUE -> blueHomePositions.contains(position)
        }
    }



    fun onCellClick(position: Pair<Int, Int>, tokensInCell: List<TokenColor>) {
        val currentPlayer = currentPlayer
        val diceRoll = currentDiceRoll.value

        // Check if the current player is null or if dice has not been rolled yet
        if (currentPlayer == null) return

        val tokenColor = currentPlayer.color

        // Check if the current player's token is in this cell
        if (tokenColor !in tokensInCell) {
            toastMessage.value = "Wrong token! Select your own token."
            return
        }

        if (diceRoll == 0) {
            toastMessage.value = "Please roll the dice!"
            return
        }

        // If the token is in home, check if a 6 is needed to move it out
        if (isTokenInHome(tokenColor, position)) {
            if (diceRoll == 6) {
                moveFromHome(tokenColor, position)  // Move token from home if dice is 6
            } else {
                toastMessage.value = "You need a 6 to move from home."
            }
        } else if (isTokenMovable(tokenColor, position)) {
            // Move the token if it is movable
            moveToken(tokenColor, position, diceRoll)
        } else {
            toastMessage.value = "Invalid move! Select a movable token."
        }
    }




    private fun sendTokenBackToHome(color: TokenColor, eliminatedPosition: Pair<Int, Int>): List<Pair<Int, Int>> {
        val homePositions = when (color) {
            TokenColor.RED -> redHomePositions
            TokenColor.GREEN -> greenHomePositions
            TokenColor.YELLOW -> yellowHomePositions
            TokenColor.BLUE -> blueHomePositions
        }

        val currentTokens = when (color) {
            TokenColor.RED -> redTokenPositions.value!!.toMutableList()
            TokenColor.GREEN -> greenTokenPositions.value!!.toMutableList()
            TokenColor.YELLOW -> yellowTokenPositions.value!!.toMutableList()
            TokenColor.BLUE -> blueTokenPositions.value!!.toMutableList()
        }

        // Find an available home cell
        val availableHomeCell = homePositions.firstOrNull { it !in currentTokens }
        availableHomeCell?.let {
            val index = currentTokens.indexOfFirst { it == eliminatedPosition }
            if (index != -1) {
                currentTokens[index] = it

                // Set toast message for elimination and points reward
                val pointsRewarded = 10
                toastMessage.value = "You eliminated ${color.name} token! Rewarded $pointsRewarded points."
            }
        }

        // Return the updated token positions
        return currentTokens
    }

    var tokens = mutableStateOf(0)

    // Collect free tokens
    fun collectTokens(amount: Int) {
        tokens.value += amount
    }

    // Method to spend tokens
    fun spendTokens(amount: Int) {
        val current = tokens.value ?: 0
        // Only spend if the user has enough tokens
        if (current >= amount) {
            tokens.value = current - amount
        }
        // If there's a scenario where you need to handle insufficient tokens
        // more directly here, you can add additional logic,
        // or simply rely on the UI check before calling this.
    }

    // Handle payment for tokens (simulated)
    fun buyTokens(amount: Int) {
        // Simulated purchase process
        tokens.value += amount
    }

}
