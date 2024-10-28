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
        if(needToLoadHistoric.value) _isEndReached.value = false
        if (_isFetching.value || _isEndReached.value) return

        viewModelScope.launch {
            _isFetching.value = true
            _loading.value = true
            _error.value = null

            try {
                val (historyItems, lastVisibleDocument) = walkRepository.getWalkHistory(userId, limit = 4, lastDocument)

                if (historyItems.isNotEmpty()) {
                    val updatedHistory = (_walkHistory.value ?: emptyList()) + historyItems
                    _walkHistory.value = updatedHistory
                    lastDocument = lastVisibleDocument
                } else {
                    _isEndReached.value = true
                }
            } catch (e: Exception) {
                _error.value = "Failed to load walk history: ${e.message}"
                Log.e("HistoricViewModel", "Error loading walk history", e)
            } finally {
                _isFetching.value = false
                _loading.value = false
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