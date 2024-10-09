package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.repositories.WalkRepository
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

    private var lastDocument: DocumentSnapshot? = null
    private var isFetching = false
    private var isEndReached = false

    fun loadWalkHistory(userId: String) {
        if (isFetching || isEndReached) return
        isFetching = true

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
                    isEndReached = true
                }
            } catch (e: Exception) {
                _error.value = e.message
                _walkHistory.value = emptyList()
            } finally {
                isFetching = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}