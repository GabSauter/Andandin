package com.example.walkapp.views.walkscreen.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.walkapp.common.avatarOptions
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
fun Map(userLocation: LatLng?, avatarIndex: Int, pathPoints: List<LatLng>, isWalking: Boolean){

    val avatarDrawable = avatarOptions[avatarIndex]
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: LatLng(-25.09, -50.17), 17f
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
            maxZoomPreference = 18f,
            minZoomPreference = 12f
        )
    ) {
        userLocation?.let { location ->
            Marker(
                state = MarkerState(position = location),
                icon = BitmapDescriptorFactory.fromResource(avatarDrawable),
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 19f)
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