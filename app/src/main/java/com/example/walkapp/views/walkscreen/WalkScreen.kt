package com.example.walkapp.views.walkscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.walkapp.models.Level
import com.example.walkapp.models.User
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.LocationViewModel
import com.example.walkapp.views.walkscreen.components.HamburgerMenuButton
import com.example.walkapp.views.walkscreen.components.MapScreenContent
import com.example.walkapp.views.walkscreen.components.PermissionRequestUI
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WalkScreen(
    navController: NavHostController,
    onSignOut: () -> Unit,
    userData: User,
    level: Level
) {
    val locationViewModel: LocationViewModel = koinViewModel()
    val userLocation by locationViewModel.userLocation.collectAsState()
    val pathPoints by locationViewModel.pathPoints.collectAsState()
    val isWalking by locationViewModel.isTracking.collectAsState()
    val totalDistance by locationViewModel.totalDistance.collectAsState()
    val elapsedTime by locationViewModel.elapsedTime.collectAsState()
    val loading by locationViewModel.loading.collectAsState()
    val walkSavedSuccessfully by locationViewModel.walkSavedSuccessfully.collectAsState()
    val haveExceptionOnSaveWalk by locationViewModel.haveExceptionOnSaveWalk.collectAsState()
    val walkDontSavedSuccessfully by locationViewModel.walkDontSavedSuccessfully.collectAsState()

    val locationPermissionState =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else {
            locationViewModel.startLocationUpdates()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!locationPermissionState.status.isGranted) {
            PermissionRequestUI(
                locationPermissionState = locationPermissionState
            )
        } else if (userLocation == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        } else {
            MapScreenContent(
                navController = navController,
                userLocation = userLocation,
                avatarIndex = userData.avatarIndex,
                pathPoints = pathPoints,
                isWalking = isWalking,
                totalDistance = totalDistance,
                elapsedTime = elapsedTime,
                startWalkingService = { context ->
                    locationViewModel.startWalkingService(
                        context,
                        userData.id
                    )
                },
                stopWalkingService = { context ->
                    run {
                        locationViewModel.stopWalkingService(
                            context,
                            userData.id
                        )
                    }
                },
                loading = loading
            )
            if (!isWalking) {
                TopWalkScreen(level, navController, onSignOut)
                if (walkSavedSuccessfully) {
                    SuccessMessage { locationViewModel.dismissSuccessMessage() }
                }
                if (haveExceptionOnSaveWalk) {
                    ExceptionMessage { locationViewModel.dismissExceptionMessage() }
                }
                if (walkDontSavedSuccessfully) {
                    FailedMessage { locationViewModel.dismissFailedMessage() }
                }
            }

        }
    }
}

@Composable
fun TopWalkScreen(level: Level, navController: NavHostController, onSignOut: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                clip = false
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = "Nv: ${level.level}")
        LinearProgressIndicator(
            progress = { level.progressPercentage.toFloat() / 100 },
            modifier = Modifier
                .padding(8.dp)
                .height(8.dp)
                .clickable {}
                .weight(1f),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        HamburgerMenuButton(
            onEditClick = {
                navController.navigate(Screen.UserForm.route) {
                    launchSingleTop = true
                }
            },
            onSignOut = onSignOut
        )
    }
}

@Composable
fun SuccessMessage(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Text(
                text = "Parabéns! Você deu mais um passo rumo aos seus objetivos. Continue caminhando, cada metro conta!",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ExceptionMessage(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Text(
                text = "Houve um erro ao salvar a caminhada.",
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun FailedMessage(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.medium)
                .padding(16.dp)
        ) {
            Text(
                text = "É necessário caminhar pelo menos 10 metros e estar por pelo menos 10 segundos para salvar a caminhada.",
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}