package com.example.walkapp.views

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.HomeViewModel
import com.example.walkapp.viewmodels.LocationViewModel
import com.example.walkapp.views.components.HamburgerMenuButton
import com.example.walkapp.views.components.Map
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WalkScreen(navController: NavHostController, authUser: FirebaseUser?, onSignOut: () -> Unit) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val locationViewModel: LocationViewModel = koinViewModel()

    val loading by homeViewModel.loading.collectAsState()
    val userData by homeViewModel.user.collectAsState()
    val userLocation by locationViewModel.userLocation.collectAsState()
    val isLocationUpdating by locationViewModel.isLocationUpdating.collectAsState()

    val avatarIndex = userData?.get("avatarIndex") as? Int ?: 0

    LaunchedEffect(true) {
        if (authUser != null && userData == null && !loading) {
            homeViewModel.loadUserData(authUser.uid)
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
        when {
            loading || userData == null -> CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )

            !locationPermissionState.status.isGranted -> {
                PermissionRequestUI(locationPermissionState, isLocationUpdating, locationViewModel)
            }

            else -> {
                MapScreenContent(
                    navController = navController,
                    userLocation = userLocation,
                    avatarIndex = avatarIndex,
                    isLocationUpdating = isLocationUpdating,
                    locationViewModel = locationViewModel
                )
            }
        }

        HamburgerMenuButton(
            onEditClick = {},
            onSignOut = { onSignOut() }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestUI(
    locationPermissionState: PermissionState,
    isLocationUpdating: Boolean,
    locationViewModel: LocationViewModel
) {
    if (isLocationUpdating) {
        locationViewModel.stopLocationUpdates()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Para que o app funcione normalmente é preciso que você permita o acesso a sua localização",
                textAlign = TextAlign.Center,
            )

            val context = LocalContext.current

            if (!locationPermissionState.status.shouldShowRationale) {
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text(text = "Abrir configurações do aplicativo")
                }
            } else {
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text(text = "Permitir acesso a minha localização")
                }
            }
        }
    }

}

@Composable
fun MapScreenContent(
    navController: NavHostController,
    userLocation: LatLng?,
    avatarIndex: Int,
    isLocationUpdating: Boolean,
    locationViewModel: LocationViewModel
) {
    if (!isLocationUpdating) {
        locationViewModel.startLocationUpdates()
    }

    Map(
        navController = navController,
        userLocation = userLocation,
        avatarIndex = avatarIndex
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                // Handle start walking logic
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = "Começar caminhada")
        }
    }
}