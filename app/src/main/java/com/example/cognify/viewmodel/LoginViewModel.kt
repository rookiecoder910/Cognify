package com.example.cognify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognify.data.login
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(login())
    val uiState: StateFlow<login> = _uiState

    private val auth = FirebaseAuth.getInstance()

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun loginUser() {
        val (email, password) = _uiState.value
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill all fields")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _uiState.value = if (task.isSuccessful)
                        _uiState.value.copy(isLoading = false, success = true)
                    else
                        _uiState.value.copy(isLoading = false, error = task.exception?.message)
                }
        }
    }

    fun registerUser(onComplete: (Boolean, String?) -> Unit) {
        val (email, password) = _uiState.value
        if (email.isBlank() || password.isBlank()) {
            onComplete(false, "Please fill all fields")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (task.isSuccessful) onComplete(true, null)
                else onComplete(false, task.exception?.message)
            }
    }

    fun sendPasswordReset(onComplete: (Boolean, String?) -> Unit) {
        val email = _uiState.value.email
        if (email.isBlank()) {
            onComplete(false, "Please enter your email first")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (task.isSuccessful) onComplete(true, null)
                else onComplete(false, task.exception?.message)
            }
    }
}
