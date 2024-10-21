package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.exceptions.GroupDoesNotExistException
import com.example.walkapp.exceptions.GroupNameAlreadyExistsException
import com.example.walkapp.exceptions.IncorrectGroupNameOrPasswordException
import com.example.walkapp.models.Group
import com.example.walkapp.models.User
import com.example.walkapp.repositories.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnterGroupViewModel(private val groupRepository: GroupRepository) : ViewModel() {
    private val _groupName = MutableStateFlow("")
    val groupName: StateFlow<String> = _groupName

    private val _groupPassword = MutableStateFlow("")
    val groupPassword: StateFlow<String> = _groupPassword

    private val _userPartOfGroup = MutableStateFlow(false)
    val userPartOfGroup: StateFlow<Boolean> = _userPartOfGroup

    private val _loadingUserPartOfGroup = MutableStateFlow(false)
    val loadingUserPartOfGroup: StateFlow<Boolean> = _loadingUserPartOfGroup

    private val _loadingJoinOrCreateGroup = MutableStateFlow(false)
    val loadingJoinOrCreateGroup: StateFlow<Boolean> = _loadingJoinOrCreateGroup

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun updateGroupName(name: String) {
        _groupName.value = name
    }

    fun updateGroupPassword(password: String) {
        _groupPassword.value = password
    }

    fun createGroup(name: String, password: String, userId: String, userData: User) {
        if (name.isEmpty() || password.isEmpty()) {
            _error.value = "Preencha todos os campos"
            return
        }
        if (password.length < 6) {
            _error.value = "A senha deve ter no mínimo 6 caracteres"
            return
        }
        viewModelScope.launch {
            _loadingJoinOrCreateGroup.value = true
            try {
                val group = Group(name, password)
                groupRepository.createGroup(userId, group, userData)
                _userPartOfGroup.value = true
            } catch (e: GroupNameAlreadyExistsException) {
                _error.value = "Nome de grupo já existe"
            } catch (e: Exception) {
                _error.value = "Houve um erro ao criar o grupo"
            } finally {
                _loadingJoinOrCreateGroup.value = false
            }
        }
    }

    fun joinGroup(name: String, password: String, userId: String, userData: User) {
        val group = Group(name, password)

        viewModelScope.launch {
            _loadingJoinOrCreateGroup.value = true
            try {
                groupRepository.joinGroup(userId, group, userData)
                _userPartOfGroup.value = true
            } catch (e: GroupDoesNotExistException) {
                _error.value = "Grupo não encontrado"
            } catch (e: IncorrectGroupNameOrPasswordException) {
                _error.value = "Nome ou senha incorretos"
            } catch (e: Exception) {
                _error.value = "Houve um erro ao entrar no grupo"
            } finally {
                _loadingJoinOrCreateGroup.value = false
            }
        }
    }

    fun isUserPartOfGroup(userId: String) {
        _loadingUserPartOfGroup.value = true
        viewModelScope.launch {
            try {
                val isPartOfGroup = groupRepository.isUserPartOfGroup(userId)
                _userPartOfGroup.value = isPartOfGroup
            } catch (e: Exception) {
                _error.value = "Houve um erro ao verificar se o usuário está no grupo"
            } finally {
                _loadingUserPartOfGroup.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}