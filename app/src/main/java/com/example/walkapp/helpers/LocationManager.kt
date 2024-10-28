package com.example.walkapp.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LocationManager {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isInitialized = false

    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState

    private var isLocationUpdatesStarted = false

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 3000
    ).apply {
        setMinUpdateIntervalMillis(3000)
        setMaxUpdateDelayMillis(12000)
        setWaitForAccurateLocation(false)
    }.build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.firstOrNull()?.let { location ->
                _locationState.value = location
            }
        }
    }

    fun initialize(context: Context) {
        if (!isInitialized) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            isInitialized = true
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (isLocationUpdatesStarted) return

        Log.d("LocationManager", "Starting location updates")
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        isLocationUpdatesStarted = true
    }

    fun stopLocationUpdates() {
        if (!isLocationUpdatesStarted) return

        Log.d("LocationManager", "Stopping location updates")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isLocationUpdatesStarted = false
    }
}