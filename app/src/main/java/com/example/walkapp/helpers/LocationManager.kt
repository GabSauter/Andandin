package com.example.walkapp.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
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
        Priority.PRIORITY_HIGH_ACCURACY, 2500
    ).apply {
        setMinUpdateIntervalMillis(1500)
        setMaxUpdateDelayMillis(12000)
        setWaitForAccurateLocation(true)
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

    fun startLocationUpdates(context: Context) {
        if (isLocationUpdatesStarted) return

        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED &&
            coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationManager", "Location permission not granted.")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                _locationState.value = it
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        isLocationUpdatesStarted = true
    }

    fun stopLocationUpdates() {
        if (!isLocationUpdatesStarted) return

        fusedLocationClient.removeLocationUpdates(locationCallback)
        isLocationUpdatesStarted = false
    }
}