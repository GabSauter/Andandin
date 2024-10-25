package com.example.walkapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.Level
import com.example.walkapp.models.User
import com.example.walkapp.repositories.LevelRepository
import com.example.walkapp.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val levelRepository: LevelRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _level = MutableStateFlow<Level?>(null)
    val level: StateFlow<Level?> = _level

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loadingUserData = MutableStateFlow(false)
    val loadingUserData: StateFlow<Boolean> = _loadingUserData

    private val _loadingLevel = MutableStateFlow(false)
    val loadingLevel: StateFlow<Boolean> = _loadingLevel

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _loadingUserData.value = true
            try {
                _user.value = userRepository.getUser(userId)
                _level.value = levelRepository.calculateLevel(user.value!!.xp)
            } catch (e: Exception) {
                _user.value = null
                _error.value = "Houve um erro ao tentar carregar os dados do usuário."
            } finally {
                _loadingUserData.value = false
            }
        }
    }

//    fun getLevel(userId: String) {
//        viewModelScope.launch {
//            try {
//                _loadingLevel.value = true
//                val level = levelRepository.getLevel(userId)
//                _level.value = level
//            } catch (e: Exception) {
//                _error.value = "Houve um erro ao tentar carregar o nível."
//            }finally {
//                _loadingLevel.value = false
//            }
//        }
//    }
}