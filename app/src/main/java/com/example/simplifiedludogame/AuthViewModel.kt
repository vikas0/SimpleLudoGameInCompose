package com.example.simplifiedludogame.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _retryCount = MutableLiveData(0)
    val retryCount: LiveData<Int> get() = _retryCount

    fun incrementRetryCount() {
        _retryCount.value = (_retryCount.value ?: 0) + 1
    }
    private val firebaseAuth = FirebaseAuth.getInstance()

    // Tracks if the user is signed in
    val isUserSignedIn = MutableLiveData(firebaseAuth.currentUser != null)

    // LiveData to signal when to trigger Google Sign-In
    private val _triggerSignIn = MutableLiveData<Boolean>()
    val triggerSignIn: LiveData<Boolean> get() = _triggerSignIn

    fun onSignInClicked() {

         incrementRetryCount()
        _triggerSignIn.value = true
    }

    fun onSignInComplete() {
        isUserSignedIn.value = firebaseAuth.currentUser != null
    }

    fun signOut() {
        firebaseAuth.signOut()
        isUserSignedIn.value = false
    }
}