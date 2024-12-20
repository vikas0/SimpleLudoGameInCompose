package com.example.simplifiedludogame.preference

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val preferences: SharedPreferences = context.getSharedPreferences("LudoPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PLAYER_COUNT = "player_count"
    }

    // Save player count (2 or 4 players)
    fun savePlayerCount(playerCount: Int) {
        preferences.edit().putInt(KEY_PLAYER_COUNT, playerCount).apply()
    }

    // Get player count (default is 2 players)
    fun getPlayerCount(): Int {
        return preferences.getInt(KEY_PLAYER_COUNT, 4)
    }

    // Clear player selection state
    fun clearPlayersSelected() {
        preferences.edit().remove(KEY_PLAYER_COUNT).apply()
    }

    fun saveLastSyncedState(stateJson: String) {
        preferences.edit().putString("last_synced_state", stateJson).apply()
    }

    fun getLastSyncedState(): String? {
        return preferences.getString("last_synced_state", null)
    }

}
