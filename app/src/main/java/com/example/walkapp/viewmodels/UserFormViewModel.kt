package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val dateOfBirth: String = "01/01/2020",
    val errorDateOfBirth: String? = null,
    val walksRegularly: Boolean = false,
    val recommendation: String = "",
    val walkingGoal: String = "",
    val errorWalkingGoal: String? = null,
    val isLoading: Boolean = false,
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

    fun validateForm() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val currentState = _uiState.value

            validateNickname(currentState.nickname)
            validateDateOfBirth(currentState.dateOfBirth)
            validateWalkingGoal(currentState.walkingGoal)

            _uiState.update {
                it.copy(isLoading = false)
            }
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

    fun onSubmit(userId: String) {
        try {
            _uiState.update {
                it.copy(isLoading = true)
            }
            saveUserData(userId)
            _uiState.update {
                it.copy(isLoading = false)
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isLoading = false, errorSubmit = e.message)
            }
        }
    }

    suspend fun loadUserData(userId: String): Map<String, Any>? {
        _uiState.update {
            it.copy(isLoading = true)
        }
        val userData = userRepository.getUser(userId)
        if (!userData.isNullOrEmpty()) {
            _uiState.update {
                it.copy(
                    nickname = userData["nickname"] as? String ?: "",
                    dateOfBirth = userData["dateOfBirth"] as? String ?: "",
                    walksRegularly = userData["walksRegularly"] as Boolean,
                    walkingGoal = userData["walkingGoal"] as? String ?: "",
                    errorNickname = null,
                    errorDateOfBirth = null,
                    errorWalkingGoal = null,
                    errorSubmit = null
                )
            }
        } else {
            _uiState.update {
                it.copy(errorSubmit = "Usuário não encontrado.")
            }
        }
        _uiState.update {
            it.copy(isLoading = false)
        }
        return userData
    }

    private fun saveUserData(userId: String) {
        val userData = mapOf(
            "nickname" to _uiState.value.nickname,
            "dateOfBirth" to _uiState.value.dateOfBirth,
            "walksRegularly" to _uiState.value.walksRegularly as Any,
            "walkingGoal" to _uiState.value.walkingGoal,
            "avatarIndex" to 1
        )

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorSubmit = null)
                userRepository.saveUserData(userId, userData)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorSubmit = e.message)
            }
        }
    }
}