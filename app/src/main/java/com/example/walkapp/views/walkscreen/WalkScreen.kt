package com.example.walkapp.views.walkscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.walkapp.common.avatarOptions
import com.example.walkapp.models.Level
import com.example.walkapp.models.User
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.HomeViewModel
import com.example.walkapp.viewmodels.WalkViewModel
import com.example.walkapp.viewmodels.LocationViewModel
import com.example.walkapp.views.walkscreen.components.RecommendationDialog
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
    val walkViewModel: WalkViewModel = koinViewModel()
    val userLocation by walkViewModel.userLocation.collectAsState()

    val locationViewModel: LocationViewModel = koinViewModel()
    val pathPoints by locationViewModel.pathPoints.collectAsState()
    val isWalking by locationViewModel.isTracking.collectAsState()
    val totalDistance by locationViewModel.totalDistance.collectAsState()
    val elapsedTime by locationViewModel.elapsedTime.collectAsState()
    val loading by locationViewModel.loading.collectAsState()

    var showRecommendation by remember { mutableStateOf(false) }

    val locationPermissionState =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (userLocation == null) {
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

            if (!locationPermissionState.status.isGranted) {
                PermissionRequestUI(
                    locationPermissionState = locationPermissionState
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
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
                                .clickable {
                                    navController.navigate(Screen.StoryList.route)
                                }
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

                    Spacer(modifier = Modifier.height(4.dp))

                    if (!isWalking) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = MaterialTheme.shapes.medium
                                ),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Image(
                                painter = painterResource(id = avatarOptions[userData!!.avatarIndex]),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(32.dp)
                            )
                            Column() {
                                Text(
                                    text = userData.nickname,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(text = "Meta: ${userData.walkingGoal} min/sem.")
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(onClick = {

                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar dados",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = {
                                    showRecommendation = true
                                }) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Mostrar info das recomendações da OMS",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                RecommendationDialog(
                    showRecommendation,
                    closeDialog = { showRecommendation = false })
            }
        }
    }
}