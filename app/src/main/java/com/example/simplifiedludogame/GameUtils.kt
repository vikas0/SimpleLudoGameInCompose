package com.example.simplifiedludogame

import androidx.lifecycle.MutableLiveData
import com.example.simplifiedludogame.model.TokenColor

fun resetTokenPositions(
    redTokens: MutableLiveData<List<Pair<Int, Int>>>,
    greenTokens: MutableLiveData<List<Pair<Int, Int>>>,
    yellowTokens: MutableLiveData<List<Pair<Int, Int>>>,
    blueTokens: MutableLiveData<List<Pair<Int, Int>>>
) {
    redTokens.value = RED_HOME_POSITIONS
    greenTokens.value = GREEN_HOME_POSITIONS
    yellowTokens.value = YELLOW_HOME_POSITIONS
    blueTokens.value = BLUE_HOME_POSITIONS
}

fun isSafeCell(position: Pair<Int, Int>): Boolean {
    return position in SAFE_CELLS
}

fun isInFinalPosition(tokenColor: TokenColor, position: Pair<Int, Int>): Boolean {
    return when (tokenColor) {
        TokenColor.RED -> position == RED_SPECIFIC_PATH.last()
        TokenColor.GREEN -> position == GREEN_SPECIFIC_PATH.last()
        TokenColor.YELLOW -> position == YELLOW_SPECIFIC_PATH.last()
        TokenColor.BLUE -> position == BLUE_SPECIFIC_PATH.last()
    }
}

fun isInHomeArea(tokenColor: TokenColor, position: Pair<Int, Int>): Boolean {
    return when (tokenColor) {
        TokenColor.RED -> position in RED_HOME_POSITIONS
        TokenColor.GREEN -> position in GREEN_HOME_POSITIONS
        TokenColor.YELLOW -> position in YELLOW_HOME_POSITIONS
        TokenColor.BLUE -> position in BLUE_HOME_POSITIONS
    }
}
