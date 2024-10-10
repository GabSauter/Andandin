package com.example.walkapp.viewmodels

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walkapp.repositories.UserRepository
import com.example.walkapp.services.WalkingService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class LocationViewModel : ViewModel() {
    val isTracking: StateFlow<Boolean> = WalkingService.isTracking
    val pathPoints: StateFlow<List<LatLng>> = WalkingService.pathPoints
    val totalDistance: StateFlow<Int> = WalkingService.totalDistance
    val elapsedTime: StateFlow<Long> = WalkingService.elapsedTime
    val loading: StateFlow<Boolean> = WalkingService.loading

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
}