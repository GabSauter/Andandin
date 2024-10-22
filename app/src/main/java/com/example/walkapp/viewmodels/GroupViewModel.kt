package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.Group
import com.example.walkapp.models.GroupUser
import com.example.walkapp.models.GroupUserWalk
import com.example.walkapp.repositories.GroupRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(private val groupRepository: GroupRepository, private val userId: String): ViewModel() {

    private val _group = MutableStateFlow<Group?>(null)
    val group: StateFlow<Group?> = _group

    private val _groupUsers = MutableStateFlow<List<GroupUser>>(emptyList())
    val groupUsers: StateFlow<List<GroupUser>> = _groupUsers

    private val _groupUsersWalks = MutableStateFlow<List<GroupUserWalk>>(emptyList())
    val groupUsersWalks: StateFlow<List<GroupUserWalk>> = _groupUsersWalks

    private val _userPartOfGroup = MutableStateFlow(false)
    val userPartOfGroup: StateFlow<Boolean> = _userPartOfGroup

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var lastDocument: DocumentSnapshot? = null
    private var isFetching = false
    private var isEndReached = false

    init {
        loadGroupAndUsers(userId)
    }

    fun leaveGroup(userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                groupRepository.leaveGroup(userId)
                _userPartOfGroup.value = false
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private fun loadGroupAndUsers(userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val group = groupRepository.getUserGroup(userId)
                _group.value = group

                if (group != null) {
                    _userPartOfGroup.value = true
                    val users = groupRepository.getGroupUsers(group.name)
                    _groupUsers.value = users

                    loadUserWalks(group.name)
                } else {
                    _userPartOfGroup.value = false
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadUserWalks(groupName: String) {
        if (isFetching || isEndReached) return
        isFetching = true

        viewModelScope.launch {
            try {
                val (userWalks, newLastDocument) = groupRepository.getPaginatedUserWalks(groupName)

                if (userWalks.isNotEmpty()) {
                    lastDocument = newLastDocument
                }

                _groupUsersWalks.value = _groupUsersWalks.value.plus(userWalks)
                if (newLastDocument == null || userWalks.isEmpty()) {
                    isEndReached = true
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
                isFetching = false
            }
        }
    }
}