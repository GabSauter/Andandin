package com.example.walkapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.User
import com.example.walkapp.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WalkViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _user.value = userRepository.getUser(userId)
                Log.d("WalkViewModel", "User data loaded: ${_user.value}")
            } catch (e: Exception) {
                _user.value = null
                _error.value = "Houve um erro ao tentar carregar os dados do usuário."
            } finally {
                _loading.value = false
            }
        }
    }
}