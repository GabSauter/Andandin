package com.example.walkapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.helpers.LocationManager
import com.example.walkapp.repositories.UserRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<Map<String, Any>?>(null)
    val user: StateFlow<Map<String, Any>?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _user.value = userRepository.getUser(userId)
                Log.d("HomeViewModel", "User data loaded successfully: ${_user.value}")
            } catch (e: Exception) {
                _user.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}