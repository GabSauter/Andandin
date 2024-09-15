package com.example.walkapp.views.walkscreen

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.walkapp.viewmodels.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.shouldShowRationale

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