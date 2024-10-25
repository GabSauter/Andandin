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
    val dateOfBirth: String = "01/01/2000",
    val errorDateOfBirth: String? = null,
    val walksRegularly: Boolean = true,
    val recommendation: String = "150 minutos por semana.",
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

    fun updateDateOfBirth(newDateOfBirth: String) {
        _uiState.update {
            it.copy(dateOfBirth = newDateOfBirth, errorDateOfBirth = null)
        }
        val age = calculateAge(newDateOfBirth)

        if (age != null) {
            updateRecommendation(age, _uiState.value.walksRegularly)
        }
    }

    fun updateWalksRegularly(regular: Boolean) {
        _uiState.update {
            it.copy(walksRegularly = regular)
        }

        if (_uiState.value.dateOfBirth != "") {
            val age = calculateAge(_uiState.value.dateOfBirth)
            updateRecommendation(age!!, _uiState.value.walksRegularly)
        }
    }

    private fun updateRecommendation(age: Int, walksRegularly: Boolean) {
        var recommendation: String = if (age < 65) {
            "150 minutos por semana."
        } else {
            "120 minutos por semana."
        }

        if (!walksRegularly) {
            recommendation += " Para pessoas que não caminham regularmente, começar mais devagar e ir aumentando o ritmo é recomendavel."
        }
        _uiState.update {
            it.copy(recommendation = recommendation)
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
                    _uiState.value.errorDateOfBirth == null &&
                    _uiState.value.errorWalkingGoal == null
                ) {
                    saveUserData(userId)
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

    private suspend fun saveUserData(userId: String) {
        val userData = User(
            id = userId,
            nickname = _uiState.value.nickname,
//            dateOfBirth = _uiState.value.dateOfBirth,
//            walksRegularly = _uiState.value.walksRegularly,
//            walkingGoal = _uiState.value.walkingGoal,
//            avatarIndex = 1
        )
        try {
            _uiState.value = _uiState.value.copy(loading = true, errorSubmit = null)
            userRepository.saveUserData(userData)
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
        validateDateOfBirth(currentState.dateOfBirth)
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
        val isUnique = userRepository.isNicknameUnique(nickname)
        if (!isUnique) {
            _uiState.update {
                it.copy(errorNickname = "Este apelido já está em uso.")
            }
        }
    }

    private fun validateDateOfBirth(dateOfBirth: String) {
        if (dateOfBirth.isBlank()) {
            _uiState.update {
                it.copy(errorDateOfBirth = "Preencha o campo data de nascimento.")
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

    private fun calculateAge(dateOfBirth: String): Int? {

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val birthDate = dateFormat.parse(dateOfBirth) ?: return null

        val today = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance().apply { time = birthDate }

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}