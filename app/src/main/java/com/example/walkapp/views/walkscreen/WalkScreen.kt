package com.example.walkapp.views.walkscreen

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.HomeViewModel
import com.example.walkapp.viewmodels.WalkViewModel
import com.example.walkapp.viewmodels.LocationViewModel
import com.example.walkapp.views.walkscreen.components.HamburgerMenuButton
import com.example.walkapp.views.walkscreen.components.MapScreenContent
import com.example.walkapp.views.walkscreen.components.PermissionRequestUI
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WalkScreen(navController: NavHostController, authUser: FirebaseUser?, onSignOut: () -> Unit) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val loadingUserData by homeViewModel.loadingUserData.collectAsState()
    val userData by homeViewModel.user.collectAsState()
    val level by homeViewModel.level.collectAsState()
    val loadingLevel by homeViewModel.loadingLevel.collectAsState()

    val walkViewModel: WalkViewModel = koinViewModel()
    val userLocation by walkViewModel.userLocation.collectAsState()

    val locationViewModel: LocationViewModel = koinViewModel()
    val pathPoints by locationViewModel.pathPoints.collectAsState()
    val isWalking by locationViewModel.isTracking.collectAsState()
    val totalDistance by locationViewModel.totalDistance.collectAsState()
    val elapsedTime by locationViewModel.elapsedTime.collectAsState()

    LaunchedEffect(userData) {
        if (!loadingUserData && authUser != null) {
            if (userData == null) {
                homeViewModel.loadUserData(authUser.uid)
            } else if (userData!!.isEmpty()) {
                navController.navigate(Screen.UserForm.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    LaunchedEffect(isWalking) {
        if (!isWalking && authUser != null) {
            homeViewModel.getLevel(authUser.uid)
        }
    }

    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (loadingUserData || userData == null || userLocation == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        } else {
            if(!locationPermissionState.status.isGranted){
                PermissionRequestUI(
                    locationPermissionState = locationPermissionState
                )
            }else {
                MapScreenContent(
                    navController = navController,
                    userLocation = userLocation,
                    avatarIndex = userData!!.avatarIndex,
                    pathPoints = pathPoints,
                    isWalking = isWalking,
                    totalDistance = totalDistance,
                    elapsedTime = elapsedTime,
                    startWalkingService = { context -> locationViewModel.startWalkingService(context, userData!!.id) },
                    stopWalkingService = { context ->
                        run {
                            locationViewModel.stopWalkingService(
                                context,
                                userData!!.id
                            )
                        }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(!loadingLevel || level != null){
                    Text(text = "Nv: ${level!!.level}")
                    LinearProgressIndicator(
                        progress = { level!!.progressPercentage.toFloat() / 100 },
                        modifier = Modifier
                            .padding(8.dp)
                            .height(8.dp)
                            .clickable {
                                Log.d("WalkScreen", "Ir para página de história")
                                navController.navigate(Screen.StoryList.route)
                            },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
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
    }
}