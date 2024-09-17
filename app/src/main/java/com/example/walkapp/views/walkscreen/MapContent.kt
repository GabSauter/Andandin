package com.example.walkapp.views.walkscreen

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.walkapp.services.WalkingService
import com.example.walkapp.viewmodels.LocationViewModel
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

@Composable
fun MapScreenContent(
    navController: NavHostController,
    userLocation: LatLng?,
    avatarIndex: Int,
    isLocationUpdating: Boolean,
    startLocationUpdates: () -> Unit,
    pathPoints: List<LatLng>,
    isWalking: Boolean,
    setIsWalking: (Boolean) -> Unit,
    clearPathPoints: () -> Unit,
    totalDistance: Double,
    clearTotalDistance: () -> Unit,
    elapsedTime: Long,
    startTimer: () -> Unit,
    stopTimer: () -> Unit
) {
    val context = LocalContext.current

    val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isLocationUpdating) {
            startLocationUpdates()
        }

        Map(
            navController = navController,
            userLocation = userLocation,
            avatarIndex = avatarIndex,
            pathPoints = pathPoints,
            isWalking = isWalking
        )

        Button(
            onClick = {
                if (isWalking) {
                    stopWalkingService(context)
                    clearPathPoints()
                    clearTotalDistance()
                    stopTimer()
                } else {
                    startWalkingService(context)
                    startTimer()
                }
                setIsWalking(!isWalking)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = if (isWalking) "Parar caminhada" else "Começar caminhada")
        }

        Column {
            Text(
                text = "Total Distance: %.2f km".format(totalDistance), // dividir por 100 para ser em km
                fontSize = 20.sp,
            )
            Text(
                text = "Time: %02d:%02d".format(hours, minutes),
                fontSize = 18.sp,
            )
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