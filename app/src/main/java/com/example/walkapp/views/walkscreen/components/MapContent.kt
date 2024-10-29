package com.example.walkapp.views.walkscreen.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.walkapp.navigation.Screen
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

@Composable
fun MapScreenContent(
    navController: NavHostController,
    userLocation: LatLng?,
    isWalking: Boolean,
    pathPoints: List<LatLng>,
    totalDistance: Int,
    elapsedTime: Long,
    avatarIndex: Int,
    startWalkingService: (Context) -> Unit,
    stopWalkingService: (Context) -> Unit,
    loading: Boolean
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Map(
            userLocation = userLocation,
            avatarIndex = avatarIndex,
            pathPoints = pathPoints,
            isWalking = isWalking
        )

        WalkControlButton(
            navController = navController,
            isWalking = isWalking,
            onWalkToggle = {
                toggleWalking(
                    isWalking = isWalking,
                    context = context,
                    startWalkingService = startWalkingService,
                    stopWalkingService = stopWalkingService
                )
            },
            loading = loading,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
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
    navController: NavHostController,
    isWalking: Boolean,
    onWalkToggle: () -> Unit,
    loading: Boolean,
    modifier: Modifier
) {
    if(isWalking && loading){
        Button(
            onClick = {},
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ){
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onTertiary,
            )
        }
    }else{
        Row (modifier = modifier, verticalAlignment = Alignment.CenterVertically){
            if(!isWalking){
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    onClick = {
                        navController.navigate(Screen.Historic.route)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_book),
                        contentDescription = "Histórico"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Histórico")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
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
                    contentDescription = if (isWalking) "Parar caminhada" else "Caminhar"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = if (isWalking) "Parar caminhada" else "Caminhar")
            }
        }
    }
}

@Composable
fun WalkInfo(totalDistance: Int, elapsedTime: Long, isWalking: Boolean) {
    if (isWalking) {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

        val velocity = if (elapsedTime > 0) (totalDistance / elapsedTime) * 3.6 else 0.0

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Distância: $totalDistance m")
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