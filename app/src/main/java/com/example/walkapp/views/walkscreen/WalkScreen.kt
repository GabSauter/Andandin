package com.example.walkapp.views.walkscreen

import android.util.Log
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
import com.example.walkapp.viewmodels.WalkViewModel
import com.example.walkapp.viewmodels.LocationViewModel
import com.example.walkapp.views.homescreen.HamburgerMenuButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WalkScreen(navController: NavHostController, authUser: FirebaseUser?, onSignOut: () -> Unit) {
    val walkViewModel: WalkViewModel = koinViewModel()
    val loading by walkViewModel.loading.collectAsState()
    val userData by walkViewModel.user.collectAsState()

    val locationViewModel: LocationViewModel = koinViewModel()
    val userLocation by locationViewModel.userLocation.collectAsState()
    val isLocationUpdating by locationViewModel.isLocationUpdating.collectAsState()

    LaunchedEffect(userData) {
        if (!loading && authUser != null && userData == null) {
            Log.d("WalkScreen", "Loading user data")
            walkViewModel.loadUserData(authUser.uid)
        }
    }

    LaunchedEffect(userData) {
        if (!loading && authUser != null && userData != null && userData!!.isEmpty()) {
            navController.navigate(Screen.UserForm.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    val locationPermissionState =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
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
                PermissionRequestUI(locationPermissionState, isLocationUpdating, locationViewModel)
            }else {
                MapScreenContent(
                    navController = navController,
                    userLocation = userLocation,
                    avatarIndex = 0,
                    isLocationUpdating = isLocationUpdating,
                    locationViewModel = locationViewModel
                )
            }
            HamburgerMenuButton(
                onEditClick = {
                    navController.navigate(Screen.UserForm.route) {
                        launchSingleTop = true
                    }
                },
                onSignOut = { onSignOut() }
            )
        }
    }
}