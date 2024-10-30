package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.models.Badges
import com.example.walkapp.repositories.BadgeRepository
import com.example.walkapp.services.WalkingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BadgeViewModel(private val badgeRepository: BadgeRepository): ViewModel() {
    private val _badges = MutableStateFlow<Badges?>(null)
    val badges: StateFlow<Badges?> = _badges

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val needToLoadBadges: StateFlow<Boolean> = WalkingService.needToLoadBadges

    init{
        WalkingService.setNeedToLoadBadges(true)
    }

    fun getBadges(userId: String) {
        try{
            _loading.value = true
            viewModelScope.launch {
                _badges.value = badgeRepository.getBadges(userId)
            }
            WalkingService.setNeedToLoadBadges(false)
        }catch(e: Exception){
            _error.value = "Houve um erro ao carregar as medalhas"
        }finally {
            _loading.value = false
        }
    }
}