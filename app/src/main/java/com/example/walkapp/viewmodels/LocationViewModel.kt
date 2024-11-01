package com.example.walkapp.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.helpers.LocationManager
import com.example.walkapp.services.WalkingService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val locationManager: LocationManager) : ViewModel() {
    val isTracking: StateFlow<Boolean> = WalkingService.isTracking
    val pathPoints: StateFlow<List<LatLng>> = WalkingService.pathPoints
    val totalDistance: StateFlow<Int> = WalkingService.totalDistance
    val elapsedTime: StateFlow<Long> = WalkingService.elapsedTime
    val loading: StateFlow<Boolean> = WalkingService.loading
    val walkSavedSuccessfully: StateFlow<Boolean> = WalkingService.walkSavedSuccessfully

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    fun startLocationUpdates() {
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

    fun startWalkingService(context: Context, userId: String) {
        val intent = Intent(context, WalkingService::class.java)
        intent.putExtra("userId", userId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopWalkingService(context: Context, userId: String) {
        val intent = Intent(context, WalkingService::class.java).apply {
            action = WalkingService.ACTION_STOP
        }
        intent.putExtra("userId", userId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun dismissSuccessMessage(){
        viewModelScope.launch {
            WalkingService.setWalkSavedSuccessfully(false)
        }
    }

//    override fun onCleared() {
//        super.onCleared()
//        locationManager.stopLocationUpdates() // Demora muito para pegar localização denovo
//    }
}