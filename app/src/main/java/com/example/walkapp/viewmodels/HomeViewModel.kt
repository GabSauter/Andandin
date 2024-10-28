package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.Level
import com.example.walkapp.models.User
import com.example.walkapp.repositories.UserRepository
import com.example.walkapp.services.WalkingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _level = MutableStateFlow<Level?>(null)
    val level: StateFlow<Level?> = _level

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loadingUserData = MutableStateFlow(false)
    val loadingUserData: StateFlow<Boolean> = _loadingUserData

    private val _userChanged = MutableStateFlow(false)
    val userChanged: StateFlow<Boolean> = _userChanged

    val needToLoadXp: StateFlow<Boolean> = WalkingService.needToLoadXp

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _loadingUserData.value = true
            try {
                _user.value = userRepository.getUser(userId)
                _level.value = calculateLevel(_user.value!!.xp)
            } catch (e: Exception) {
                _user.value = null
                _error.value = "Houve um erro ao tentar carregar os dados do usuário."
            } finally {
                _loadingUserData.value = false
            }
        }
    }

    private fun calculateLevel(xp: Int): Level {
        val c = 10.0
        val d = 1.5

        val currentLevel = floor(c * (ln((xp + 1).toDouble()).pow(d))).toInt()
        val currentLevelDistance = exp((currentLevel / c).pow(1 / d)) - 1

        val nextLevel = currentLevel + 1
        val nextLevelDistance = exp((nextLevel / c).pow(1 / d)) - 1
        val progressPercentage = ((xp - currentLevelDistance) /
                (nextLevelDistance - currentLevelDistance)) * 100

        return Level(currentLevel, progressPercentage)
    }

    fun setUserChanged(value: Boolean){
        _userChanged.value = value
    }

    fun setNeedToLoadXp(value: Boolean) {
        WalkingService.setNeedToLoadXp(value)
    }
}