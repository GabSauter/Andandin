package com.example.walkapp.views.walkscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val loading by homeViewModel.loading.collectAsState()
    val userData by homeViewModel.user.collectAsState()

    val walkViewModel: WalkViewModel = koinViewModel()
    val userLocation by walkViewModel.userLocation.collectAsState()

    val locationViewModel: LocationViewModel = koinViewModel()
    val pathPoints by locationViewModel.pathPoints.collectAsState()
    val isWalking by locationViewModel.isTracking.collectAsState()
    val totalDistance by locationViewModel.totalDistance.collectAsState()
    val elapsedTime by locationViewModel.elapsedTime.collectAsState()

    LaunchedEffect(userData) {
        if (!loading && authUser != null) {
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

    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(locationPermissionState) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (loading || userData == null) {
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
                    avatarIndex = userData?.avatarIndex ?: 0,
                    pathPoints = pathPoints,
                    isWalking = isWalking,
                    totalDistance = totalDistance,
                    elapsedTime = elapsedTime,
                    startWalkingService = { context -> locationViewModel.startWalkingService(context, userData!!.id) },
                    stopWalkingService = { context -> locationViewModel.stopWalkingService(context, userData!!.id) }
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