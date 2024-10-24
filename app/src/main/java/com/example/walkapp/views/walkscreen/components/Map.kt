package com.example.walkapp.views.walkscreen.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.walkapp.R
import com.example.walkapp.common.avatarOptions
import com.example.walkapp.navigation.Screen
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun Map(navController: NavHostController, userLocation: LatLng?, avatarIndex: Int, pathPoints: List<LatLng>, isWalking: Boolean){

    val avatarDrawable = avatarOptions.getOrNull(avatarIndex) ?: R.drawable.avatar1

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: LatLng(37.7749, -122.4194), 20f
        )
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            compassEnabled = false,
            zoomControlsEnabled = false
        ),
        properties = MapProperties(
            maxZoomPreference = 20f,
            minZoomPreference = 16f
        )
    ) {
        userLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                icon = BitmapDescriptorFactory.fromResource(avatarDrawable),
                onClick = {
                    navController.navigate(Screen.AvatarMaker.route)
                    true
                }
            )
        }

        if (isWalking) {
            Polyline(
                points = pathPoints,
                color = MaterialTheme.colorScheme.primary,
                width = 10f
            )
        }
    }
}