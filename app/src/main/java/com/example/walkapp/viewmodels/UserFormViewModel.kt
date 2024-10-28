package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.User
import com.example.walkapp.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class UserFormUiState(
    val nickname: String = "",
    val errorNickname: String? = null,
    val walkingGoal: String = "",
    val errorWalkingGoal: String? = null,
    val loading: Boolean = false,
    val errorSubmit: String? = null
)

class UserFormViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UserFormUiState())
    val uiState: StateFlow<UserFormUiState> = _uiState.asStateFlow()

    fun updateNickname(newNickname: String) {
        if (newNickname.length <= 20) {
            _uiState.update {
                it.copy(nickname = newNickname, errorNickname = null)
            }
        }
    }

    fun updateWalkingGoal(newGoal: String) {
        if (newGoal.length <= 5) {
            _uiState.update {
                it.copy(walkingGoal = newGoal, errorWalkingGoal = null)
            }
        }
    }

    fun clearErrorSubmit() {
        _uiState.update {
            it.copy(errorSubmit = null)
        }
    }

    fun onSubmit(userId: String) {
        try {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(loading = true)
                }
                validateForm()
                if (
                    _uiState.value.errorNickname == null &&
                    _uiState.value.errorWalkingGoal == null
                ) {
                    updateUserData(userId)
                }
                _uiState.update {
                    it.copy(loading = false)
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(loading = false, errorSubmit = e.message)
            }
        }
    }

    private suspend fun updateUserData(userId: String) {
        val userData = User(
            id = userId,
            nickname = _uiState.value.nickname,
            walkingGoal = _uiState.value.walkingGoal.toInt(),
            avatarIndex = 1
        )
        try {
            _uiState.value = _uiState.value.copy(loading = true, errorSubmit = null)
            userRepository.updateUserData(userId, userData)
            _uiState.value = _uiState.value.copy(loading = false)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(loading = false, errorSubmit = e.message)
        }
    }

    private suspend fun validateForm() {
        _uiState.update {
            it.copy(loading = true)
        }

        val currentState = _uiState.value
        validateNickname(currentState.nickname)
        validateWalkingGoal(currentState.walkingGoal)

        _uiState.update {
            it.copy(loading = false)
        }
    }

    private suspend fun validateNickname(nickname: String) {
        if (nickname.isBlank()) {
            _uiState.update {
                it.copy(errorNickname = "Preencha o campo apelido.")
            }
        }
//        val isUnique = userRepository.isNicknameUnique(nickname)
//        if (!isUnique) {
//            _uiState.update {
//                it.copy(errorNickname = "Este apelido já está em uso.")
//            }
//        }
    }

    private fun validateWalkingGoal(walkingGoal: String) {
        if (walkingGoal.isBlank()) {
            _uiState.update {
                it.copy(errorWalkingGoal = "Preencha o campo meta.")
            }
        }
        if (walkingGoal.toIntOrNull() == null) {
            _uiState.update {
                it.copy(errorWalkingGoal = "Meta precisa ser um número.")
            }
        } else {
            if (walkingGoal.toInt() <= 5) {
                _uiState.update {
                    it.copy(errorWalkingGoal = "Meta precisa ser maior que 5.")
                }
            }
            if (walkingGoal.toInt() > 100000) {
                _uiState.update {
                    it.copy(errorWalkingGoal = "Meta precisa ser menor que 100000.")
                }
            }
        }
    }
}