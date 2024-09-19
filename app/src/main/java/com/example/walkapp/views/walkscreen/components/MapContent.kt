package com.example.walkapp.views.walkscreen.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.walkapp.R
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

@Composable
fun MapScreenContent(
    navController: NavHostController,
    userLocation: LatLng?,
    isWalking: Boolean,
    pathPoints: List<LatLng>,
    totalDistance: Double,
    elapsedTime: Long,
    avatarIndex: Int,
    startWalkingService: (Context) -> Unit,
    stopWalkingService: (Context) -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Map(
            navController = navController,
            userLocation = userLocation,
            avatarIndex = avatarIndex,
            pathPoints = pathPoints,
            isWalking = isWalking
        )

        WalkControlButton(
            isWalking = isWalking,
            onWalkToggle = {
                toggleWalking(
                    isWalking = isWalking,
                    context = context,
                    startWalkingService = startWalkingService,
                    stopWalkingService = stopWalkingService
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            WalkInfo(totalDistance, elapsedTime, isWalking)
        }
    }
}

@Composable
fun WalkControlButton(
    isWalking: Boolean,
    onWalkToggle: () -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = onWalkToggle,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isWalking) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
            contentColor = if (isWalking) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            painter = if (isWalking) painterResource(R.drawable.ic_stop) else painterResource(R.drawable.ic_play),
            contentDescription = if (isWalking) "Parar caminhada" else "Começar caminhada"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = if (isWalking) "Parar caminhada" else "Começar caminhada")
    }
}

@Composable
fun WalkInfo(totalDistance: Double, elapsedTime: Long, isWalking: Boolean) {
    if (isWalking) {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

        val velocity = if (elapsedTime > 0) (totalDistance / elapsedTime) * 3.6 else 0.0

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Distância: %.2f m".format(totalDistance))
            Text(text = "Tempo: %02d:%02d:%02d".format(hours, minutes, seconds))
            Text(text = "Velocidade: %.2f km/h".format(velocity))
        }
    }
}

fun toggleWalking(
    isWalking: Boolean,
    context: Context,
    startWalkingService: (Context) -> Unit,
    stopWalkingService: (Context) -> Unit
) {
    if (isWalking) {
        stopWalkingService(context)
    } else {
        startWalkingService(context)
    }
}