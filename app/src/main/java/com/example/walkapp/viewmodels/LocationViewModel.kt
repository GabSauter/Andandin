package com.example.walkapp.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import com.example.walkapp.services.WalkingService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel : ViewModel() {

    val isTracking: StateFlow<Boolean> = WalkingService.isTracking
    val pathPoints: StateFlow<List<LatLng>> = WalkingService.pathPoints
    val totalDistance: StateFlow<Double> = WalkingService.totalDistance
    val elapsedTime: StateFlow<Long> = WalkingService.elapsedTime

    fun startWalkingService(context: Context) {
        val intent = Intent(context, WalkingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopWalkingService(context: Context) {
        val intent = Intent(context, WalkingService::class.java)
        context.stopService(intent)
    }

    fun addPathPoint(location: LatLng) {
        WalkingService.addPathPoint(location)
    }
}