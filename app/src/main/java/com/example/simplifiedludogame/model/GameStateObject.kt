package com.example.simplifiedludogame.model

import com.example.simplifiedludogame.GameViewModel

data class GameStateObject(
    val redTokenPositions: List<Pair<Int, Int>> = emptyList(),
    val greenTokenPositions: List<Pair<Int, Int>> = emptyList(),
    val yellowTokenPositions: List<Pair<Int, Int>> = emptyList(),
    val blueTokenPositions: List<Pair<Int, Int>> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val lastDiceRoll: Int = 0,
    val currentDiceRoll: Int = 0,
    val isDiceRollDisabled: Boolean = false,
    val isGameOver: Boolean = false,
    val winner:String = "",
    val players: List<Player> = emptyList(),
    val turnsTaken: Int = 0
){
    companion object
    {
        // Extension to convert Map to GameStateObject
        fun GameStateObject.Companion.fromMap(map: Map<String, Any>): GameStateObject {
            return GameStateObject(
                redTokenPositions = (map["redTokenPositions"] as? List<Map<String, Int>>)?.map {
                    Pair(it["first"] ?: 0, it["second"] ?: 0)
                } ?: emptyList(),
                greenTokenPositions = (map["greenTokenPositions"] as? List<Map<String, Int>>)?.map {
                    Pair(it["first"] ?: 0, it["second"] ?: 0)
                } ?: emptyList(),
                yellowTokenPositions = (map["yellowTokenPositions"] as? List<Map<String, Int>>)?.map {
                    Pair(it["first"] ?: 0, it["second"] ?: 0)
                } ?: emptyList(),
                blueTokenPositions = (map["blueTokenPositions"] as? List<Map<String, Int>>)?.map {
                    Pair(it["first"] ?: 0, it["second"] ?: 0)
                } ?: emptyList(),
                currentPlayerIndex = (map["currentPlayerIndex"] as? Number)?.toInt() ?: 0,
                lastDiceRoll = (map["lastDiceRoll"] as? Number)?.toInt() ?: 0,
                currentDiceRoll = (map["currentDiceRoll"] as? Number)?.toInt() ?: 0,
                isDiceRollDisabled = map["isDiceRollDisabled"] as? Boolean ?: false,
                isGameOver = map["isGameOver"] as? Boolean ?: false,
                players = (map["players"] as? List<Map<String, Any>>)?.map {
                    Player(
                        color = TokenColor.valueOf(it["color"] as String),
                        score = (it["score"] as? Number)?.toInt() ?: 0,
                        turnsTaken = (it["turnsTaken"] as? Number)?.toInt() ?: 0,
                        hasExtraTurn = it["hasExtraTurn"] as? Boolean ?: false
                    )
                } ?: emptyList(),
                turnsTaken = (map["turnsTaken"] as? Number)?.toInt() ?: 0
            )
        }
    }

}

// Extension to convert GameStateObject to a Map
fun GameStateObject.toMap(): Map<String, Any> {
    return mapOf(
        "redTokenPositions" to redTokenPositions,
        "greenTokenPositions" to greenTokenPositions,
        "yellowTokenPositions" to yellowTokenPositions,
        "blueTokenPositions" to blueTokenPositions,
        "currentPlayerIndex" to currentPlayerIndex,
        "lastDiceRoll" to lastDiceRoll,
        "currentDiceRoll" to currentDiceRoll,
        "isDiceRollDisabled" to isDiceRollDisabled,
        "isGameOver" to isGameOver,
        "winner" to winner,
        "players" to players.map { player ->
            mapOf(
                "color" to player.color.name,
                "score" to player.score,
                "turnsTaken" to player.turnsTaken,
                "hasExtraTurn" to player.hasExtraTurn
            )
        },
        "turnsTaken" to turnsTaken
    )
}
