package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.walkapp.helpers.CredentialHelper
import com.example.walkapp.repositories.AuthRepository

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _user = MutableStateFlow(authRepository.getCurrentUser())
    val user: StateFlow<FirebaseUser?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun signInWithGoogle(credentialHelper: CredentialHelper) {
        _loading.value = true
        val request = authRepository.buildGoogleIdTokenRequest()

        viewModelScope.launch {
            try {
                val googleIdToken = credentialHelper.getGoogleIdToken(request)
                authRepository.signInWithGoogle(googleIdToken)
                _user.value = authRepository.getCurrentUser()
            } catch (e: Exception) {
                _error.value = "Houve um erro durante o login. Por favor, tente novamente."
            } finally {
                _loading.value = false
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _user.value = null
    }

    fun clearError(){
        _error.value = null
    }
}

