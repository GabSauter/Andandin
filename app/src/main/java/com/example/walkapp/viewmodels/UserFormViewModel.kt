package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.walkapp.models.User
import com.example.walkapp.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserFormUiState(
    val nickname: String = "",
    val errorNickname: String? = null,
    val walkingGoal: String = "",
    val avatarIndex: Int = 0,
    val errorWalkingGoal: String? = null,
    val loading: Boolean = false,
    val errorSubmit: String? = null
)

class UserFormViewModel(private val userRepository: UserRepository, private val nickname: String, private val walkingGoal: String, private val avatarIndex: Int) : ViewModel() {

    private val _uiState = MutableStateFlow(UserFormUiState())
    val uiState: StateFlow<UserFormUiState> = _uiState.asStateFlow()

    init{
        _uiState.update {
            it.copy(nickname = nickname, walkingGoal = walkingGoal, avatarIndex = avatarIndex)
        }
    }

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

    fun updateAvatarIndex(newAvatarIndex: Int) {
        _uiState.update {
            it.copy(avatarIndex = newAvatarIndex)
        }
    }

    fun clearErrorSubmit() {
        _uiState.update {
            it.copy(errorSubmit = null)
        }
    }

    fun onSubmit(userId: String, setUserChanged: (Boolean) -> Unit, navController: NavHostController) {
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
                if (
                    uiState.value.errorNickname == null &&
                    uiState.value.errorWalkingGoal == null &&
                    uiState.value.errorSubmit == null
                ) {
                    setUserChanged(true)
                    navController.popBackStack()
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(errorSubmit = e.message)
            }
        }finally{
            _uiState.update {
                it.copy(loading = false)
            }
        }
    }

    private suspend fun updateUserData(userId: String) {
        val userData = User(
            id = userId,
            nickname = _uiState.value.nickname,
            walkingGoal = _uiState.value.walkingGoal.toInt(),
            avatarIndex = _uiState.value.avatarIndex
        )
        try {
            _uiState.value = _uiState.value.copy(loading = true, errorSubmit = null)
            userRepository.updateUserData(userId, userData)
            _uiState.value = _uiState.value.copy(loading = false)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(loading = false, errorSubmit = e.message)
        }
    }

    private fun validateForm() {
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

    private fun validateNickname(nickname: String) {
        if (nickname.isBlank()) {
            _uiState.update {
                it.copy(errorNickname = "Preencha o campo apelido.")
            }
        }
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