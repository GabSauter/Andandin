package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AvatarMakerViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _avatarIndex = MutableStateFlow(0)
    val avatarIndex: StateFlow<Int> = _avatarIndex

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun setAvatarIndex(index: Int) {
        _avatarIndex.value = index
    }

    fun clearError() {
        _error.value = null
    }

    fun saveAvatarIndex(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                userRepository.updateAvatarIndex(userId, _avatarIndex.value)
            } catch (e: Exception) {
                _error.value = "Erro ao salvar o avatar."
            }
            _loading.value = false
        }
    }
}