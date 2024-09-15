package com.example.walkapp.views.walkscreen

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.walkapp.services.WalkingService
import com.example.walkapp.viewmodels.LocationViewModel
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapScreenContent(
    navController: NavHostController,
    userLocation: LatLng?,
    avatarIndex: Int,
    isLocationUpdating: Boolean,
    locationViewModel: LocationViewModel
) {
    val context = LocalContext.current
    var isWalking by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isLocationUpdating) {
            locationViewModel.startLocationUpdates()
        }

        Map(
            navController = navController,
            userLocation = userLocation,
            avatarIndex = avatarIndex
        )

        Button(
            onClick = {
                if (isWalking) {
                    stopWalkingService(context)
                } else {
                    startWalkingService(context)
                }
                isWalking = !isWalking
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = if (isWalking) "Parar caminhada" else "Começar caminhada")
        }
    }
}

private fun startWalkingService(context: Context) {
    val intent = Intent(context, WalkingService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

private fun stopWalkingService(context: Context) {
    val intent = Intent(context, WalkingService::class.java)
    context.stopService(intent)
}