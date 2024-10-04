package com.example.walkapp.views.avatarmakerscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.walkapp.R
import com.example.walkapp.views.components.ErrorSnackbar
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.AvatarMakerViewModel
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@Composable
fun AvatarMakerScreen(
    navController: NavHostController,
    authUser: FirebaseUser?,
    passedAvatarIndex: Int?
) {
    val avatarMakerViewModel: AvatarMakerViewModel = koinViewModel()

    LaunchedEffect(passedAvatarIndex) {
        passedAvatarIndex?.let {
            avatarMakerViewModel.setAvatarIndex(it)
        }
    }

    val avatarIndex by avatarMakerViewModel.avatarIndex.collectAsState()
    val errorSubmit by avatarMakerViewModel.error.collectAsState()
    val loading by avatarMakerViewModel.loading.collectAsState()
    val onSubmit = {
        avatarMakerViewModel.saveAvatarIndex(authUser!!.uid)
        if (avatarMakerViewModel.error.value == null) {
            navController.navigate(Screen.Walk.route) {
                popUpTo(Screen.Walk.route) { inclusive = true }
            }
        }
    }

    val avatarOptions = listOf(
        R.drawable.avatar1,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4,
        R.drawable.avatar5
    )
    Box {
        Image(
            painter = painterResource(id = R.drawable.img_forest_background),
            contentDescription = "Imagem de floresta",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            item {
                Text(
                    text = "Andandin",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(32.dp)
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Avatar", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        Image(
                            painter = painterResource(id = avatarOptions[avatarIndex]),
                            contentDescription = "Body"
                        )
                    }

                    AvatarControlRow(
                        "Avatar",
                        avatarOptions.size,
                        avatarIndex,
                        { avatarMakerViewModel.setAvatarIndex((avatarIndex - 1 + avatarOptions.size) % avatarOptions.size) },
                        { avatarMakerViewModel.setAvatarIndex((avatarIndex + 1) % avatarOptions.size) }
                    )
                }
            }
            item {
                Box(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Button(
                        onClick = {
                            if (errorSubmit == null) onSubmit()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Continuar")
                        }
                    }
                }
            }
        }
        ErrorSnackbar(
            errorMessage = errorSubmit,
            onDismiss = {
                avatarMakerViewModel.clearError()
            }
        )
    }
}

@Composable
fun AvatarControlRow(
    label: String,
    total: Int,
    currentIndex: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPrevious) {
            Text("<")
        }
        Text(
            text = "$label ${currentIndex + 1} / $total",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Button(onClick = onNext) {
            Text(">")
        }
    }
}