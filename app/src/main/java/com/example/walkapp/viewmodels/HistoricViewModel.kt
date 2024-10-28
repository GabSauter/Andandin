package com.example.walkapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.repositories.WalkRepository
import com.example.walkapp.services.WalkingService
import com.example.walkapp.views.historicscreen.WalkHistoryItem
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoricViewModel(private val walkRepository: WalkRepository): ViewModel() {
    private val _walkHistory = MutableStateFlow<List<WalkHistoryItem>?>(null)
    val walkHistory: StateFlow<List<WalkHistoryItem>?> = _walkHistory

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val needToLoadHistoric: StateFlow<Boolean> = WalkingService.needToLoadHistoric

    private var lastDocument: DocumentSnapshot? = null

    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> = _isFetching

    private val _isEndReached = MutableStateFlow(false)
    val isEndReached: StateFlow<Boolean> = _isEndReached

    fun loadWalkHistory(userId: String) {
        Log.d("HistoricViewModel", "loadWalkHistory 1")
        if(needToLoadHistoric.value) _isEndReached.value = false
        if (isFetching.value || isEndReached.value) return
        _isFetching.value = true

        Log.d("HistoricViewModel", "loadWalkHistory 2")

        viewModelScope.launch {
            try {
                val (history, newLastDocument) = walkRepository.getWalkHistory(userId, 8, lastDocument)

                if (history.isNotEmpty()) {
                    lastDocument = newLastDocument
                }

                if (_walkHistory.value == null) {
                    _walkHistory.value = history
                } else {
                    _walkHistory.value = _walkHistory.value?.plus(history)
                }
                if (newLastDocument == null || history.isEmpty()) {
                    _isEndReached.value = true
                }
            } catch (e: Exception) {
                _error.value = e.message
                _walkHistory.value = emptyList()
            } finally {
                _isFetching.value = false
            }
        }
    }

    fun setNeedToLoadHistoric(value: Boolean) {
        WalkingService.setNeedToLoadHistoric(value)
    }

    fun clearError() {
        _error.value = null
    }
}