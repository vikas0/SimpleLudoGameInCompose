package com.example.simplifiedludogame

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.simplifiedludogame.uiCompose.AppContent
import com.example.simplifiedludogame.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val gameViewModel: GameViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Google sign-in failed", e)
            }
        } else if (result.resultCode == RESULT_CANCELED) {
            Log.e("GoogleSignIn", "User canceled the account selection")
            // Show a message to the user or log the cancellation
            Toast.makeText(this, "Sign-in canceled. Please try again.", Toast.LENGTH_SHORT).show()
        } else {
            Log.e("GoogleSignIn", "Unknown result code: ${result.resultCode}")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Observe LiveData to trigger Google Sign-In
        authViewModel.triggerSignIn.observe(this, Observer {
            if(it)
            signInWithGoogle()
        })

        setContent {
            AppContent(
                authViewModel = authViewModel,
                gameViewModel = gameViewModel
            )
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    authViewModel.onSignInComplete()
                    Log.d("GoogleSignIn", "Firebase Auth successful. User: ${user?.displayName}")
                } else {
                    authViewModel.onSignInComplete()
                    Log.e("GoogleSignIn", "Firebase Auth failed", task.exception)
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}