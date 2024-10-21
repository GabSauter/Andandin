package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.Group
import com.example.walkapp.models.User
import com.example.walkapp.repositories.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(private val groupRepository: GroupRepository): ViewModel() {

    private val _groupName = MutableStateFlow("")
    val groupName: StateFlow<String> = _groupName

    private val _groupPassword = MutableStateFlow("")
    val groupPassword: StateFlow<String> = _groupPassword

    private val _userPartOfGroup = MutableStateFlow(false)
    val userPartOfGroup: StateFlow<Boolean> = _userPartOfGroup

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun updateGroupName(name: String) {
        _groupName.value = name
    }

    fun updateGroupPassword(password: String) {
        _groupPassword.value = password
    }

    fun createGroup(name: String, password: String, userId: String, userData: User) {
        try{
            if(name.isEmpty() || password.isEmpty()){
                _error.value = "Preencha todos os campos"
                return
            }
            val group = Group(name, password)
            viewModelScope.launch{
                groupRepository.createGroup(userId, group, userData)
                _userPartOfGroup.value = true
            }
        }catch (e: Exception){
            _error.value = "Houve um erro ao criar o grupo"
        }
    }

    fun joinGroup(name: String, password: String, userId: String, userData: User) {
        try{
            val group = Group(name, password)
            viewModelScope.launch{
                groupRepository.joinGroup(userId, group, userData)
                _userPartOfGroup.value = true
            }
        }catch (e: Exception){
            _error.value = "Houve um erro ao entrar no grupo"
        }
    }

    fun isUserPartOfGroup(userId: String) {
        try{
            viewModelScope.launch {
                val isPartOfGroup = groupRepository.isUserPartOfGroup(userId)
                _userPartOfGroup.value = isPartOfGroup
            }
        }catch (e: Exception){
            _error.value = "Houve um erro ao verificar se o usuário está no grupo"
        }
    }
}