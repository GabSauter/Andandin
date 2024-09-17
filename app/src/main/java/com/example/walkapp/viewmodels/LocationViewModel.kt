package com.example.walkapp.viewmodels

import android.location.Location
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.helpers.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val locationManager: LocationManager): ViewModel() {

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    private val _isLocationUpdating = MutableStateFlow<Boolean>(false)
    val isLocationUpdating: StateFlow<Boolean> = _isLocationUpdating

    private val _pathPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val pathPoints: StateFlow<List<LatLng>> = _pathPoints

    private var _isWalking = MutableStateFlow(false)
    val isWalking: StateFlow<Boolean> = _isWalking

    private val _totalDistance = MutableStateFlow(0.0)
    val totalDistance: StateFlow<Double> = _totalDistance

    private var startTime = 0L
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    init {
        viewModelScope.launch {
            locationManager.locationState.collect { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    _userLocation.value = latLng

                    if (_isWalking.value) {
                        val updatedPath = _pathPoints.value + latLng
                        _pathPoints.value = updatedPath

                        if (updatedPath.size > 1) {
                            val previousPoint = updatedPath[updatedPath.size - 2]
                            val newDistance = calculateDistance(previousPoint, latLng)
                            _totalDistance.value += newDistance
                        }
                    }
                }
            }
        }

        locationManager.startLocationUpdates()
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val startLocation = Location("").apply {
            latitude = start.latitude
            longitude = start.longitude
        }

        val endLocation = Location("").apply {
            latitude = end.latitude
            longitude = end.longitude
        }

        return startLocation.distanceTo(endLocation).toDouble()
    }

    private var timerJob: Job? = null
    fun startTimer() {
        startTime = SystemClock.elapsedRealtime()
        timerJob = viewModelScope.launch {
            while (true) {
                _elapsedTime.value = SystemClock.elapsedRealtime() - startTime
                delay(60000L)
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _elapsedTime.value = 0L
    }

    fun setIsWalking(isWalking: Boolean) {
        _isWalking.value = isWalking
    }

    fun clearTotalDistance() {
        _totalDistance.value = 0.0
    }

    fun clearPathPoints() {
        _pathPoints.value = emptyList()
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