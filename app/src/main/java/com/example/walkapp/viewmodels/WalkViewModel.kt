package com.example.walkapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.helpers.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WalkViewModel(private val locationManager: LocationManager) : ViewModel() {

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    init {
        viewModelScope.launch {
            locationManager.locationState.collect { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    _userLocation.value = latLng
                }
            }
        }

        locationManager.startLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopLocationUpdates()
    }
}