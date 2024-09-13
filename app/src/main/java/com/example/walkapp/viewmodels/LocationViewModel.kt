package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.helpers.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val locationManager: LocationManager): ViewModel() {

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    private val _isLocationUpdating = MutableStateFlow<Boolean>(false)
    val isLocationUpdating: StateFlow<Boolean> = _isLocationUpdating

    fun updateUserLocation(location: LatLng) { // Função apenas para teste
        _userLocation.value = location
    }

    init {
        viewModelScope.launch {
            locationManager.locationState.collect { location ->
                location?.let {
                    _userLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
        }

        locationManager.startLocationUpdates()
    }

    fun startLocationUpdates() {
        _isLocationUpdating.value = true
        locationManager.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        _isLocationUpdating.value = false
        locationManager.stopLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        _isLocationUpdating.value = false
        locationManager.stopLocationUpdates()
    }
}