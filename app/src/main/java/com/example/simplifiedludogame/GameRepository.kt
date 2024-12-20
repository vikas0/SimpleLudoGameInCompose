package com.example.simplifiedludogame

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private var gameDataReference: DatabaseReference? = null

    private fun ensureGameDataReference(onComplete: (DatabaseReference?, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(null, "User not authenticated")
            return
        }

        val userId = currentUser.uid
        gameDataReference = database.reference.child("users").child(userId).child("currentGame")
        onComplete(gameDataReference, null)
    }


    // Save the game state
    fun saveGameState(gameState: Map<String, Any>, onComplete: (Boolean, String?) -> Unit) {
        ensureGameDataReference { reference, error ->
            if (reference != null) {
                reference.setValue(gameState)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onComplete(true, null) // Success: No error message
                        } else {
                            onComplete(false, task.exception?.localizedMessage ?: "Unknown error") // Pass exception message
                        }
                    }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.localizedMessage ?: "Unknown error during save") // Debugging error
                    }
            } else {
                onComplete(false, error)
            }
        }
    }

    fun getSavedGame(onComplete: (Map<String, Any>?, String?) -> Unit) {
        Log.d("Repository", "Fetching saved game...")
        ensureGameDataReference { reference, error ->
            if (reference != null) {
                Log.d("Repository", "Valid database reference found.")
                reference.get()
                    .addOnSuccessListener { snapshot ->
                        Log.d("Repository", "Snapshot success: ${snapshot.value}")
                        @Suppress("UNCHECKED_CAST")
                        val data = snapshot.value as? Map<String, Any>
                        onComplete(data, null)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Repository", "Snapshot failure: ${exception.localizedMessage}")
                        onComplete(null, exception.localizedMessage ?: "Unknown error during fetch")
                    }
            } else {
                Log.e("Repository", "Error retrieving reference: $error")
                onComplete(null, error ?: "Unknown reference error")
            }
        }
    }

    // Delete the saved game state
    fun deleteSavedGame(onComplete: (Boolean, String?) -> Unit) {
        ensureGameDataReference { reference, error ->
            if (reference != null) {
                reference.removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, task.exception?.localizedMessage ?: "Unknown error")
                        }
                    }
                    .addOnFailureListener { exception ->
                        onComplete(false, exception.localizedMessage ?: "Unknown error during delete")
                    }
            } else {
                onComplete(false, error)
            }
        }
    }
}
