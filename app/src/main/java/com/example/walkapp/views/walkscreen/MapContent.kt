package com.example.walkapp.views.walkscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
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