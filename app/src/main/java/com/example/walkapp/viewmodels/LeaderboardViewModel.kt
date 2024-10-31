package com.example.walkapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.LeaderboardUser
import com.example.walkapp.repositories.LeaderboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LeaderboardViewModel(private val leaderboardRepository: LeaderboardRepository) : ViewModel() {

    private val _userLeaderboard = MutableStateFlow(LeaderboardUser.emptyLeaderboardUser())
    val user: StateFlow<LeaderboardUser> = _userLeaderboard

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _leaderboardInGroup = MutableStateFlow<List<LeaderboardUser>?>(null)
    val leaderboardInGroup: StateFlow<List<LeaderboardUser>?> = _leaderboardInGroup

    private val _leaderboard = MutableStateFlow<List<LeaderboardUser>?>(null)
    val leaderboard: StateFlow<List<LeaderboardUser>?> = _leaderboard

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _selectedFilter = MutableStateFlow(0)
    val selectedFilter: StateFlow<Int> = _selectedFilter

    fun setSelectedFilter(filter: Int) {
        _selectedFilter.value = filter
    }

    suspend fun getLeaderboardForMonth() {
        _loading.value = true
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val monthAndYear = dateFormat.format(calendar.time)
        viewModelScope.launch {
            try {
                val leaderboardUser =
                    leaderboardRepository.getLeaderboardForMonth(monthAndYear)
                Log.d("LeaderboardViewModel", "Leaderboard: $leaderboardUser")
                _leaderboard.value = leaderboardUser
            } catch (e: Exception) {
                _error.value = "Houve um erro ao obter o ranking do mês."
            }finally {
                _loading.value = false
            }
        }
    }

    fun getLeaderboardForMonthInGroup(
        monthAndYear: String,
        userId: String
    ) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val leaderboardUser =
                    leaderboardRepository.getLeaderboardForMonthInGroup(monthAndYear, userId)
                _leaderboardInGroup.value = leaderboardUser
            } catch (e: Exception) {
                _error.value = "Houve um erro ao obter o ranking do mês."
            } finally {
                _loading.value = false
            }
        }
    }

    fun getUserLeaderboard(userId: String) {
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val monthAndYear = dateFormat.format(calendar.time)
        viewModelScope.launch {
            try {
                val leaderboardUser =
                    leaderboardRepository.getUserLeaderboard(userId, monthAndYear)
                _userLeaderboard.value = leaderboardUser
            } catch (e: Exception) {
                _error.value = "Houve um erro ao obter o ranking do usuário."
            }
        }
    }
}