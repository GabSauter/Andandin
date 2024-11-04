package com.example.walkapp.views.userformscreen

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.walkapp.R
import com.example.walkapp.common.avatarOptions
import com.example.walkapp.models.User
import com.example.walkapp.views.components.ErrorSnackbar
import com.example.walkapp.viewmodels.UserFormViewModel
import com.example.walkapp.views.userformscreen.components.CustomOutlinedTextField
import com.example.walkapp.views.userformscreen.components.NumberTextField
import com.example.walkapp.views.walkscreen.components.RecommendationDialog
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserFormScreen(
    navController: NavHostController,
    authUser: FirebaseUser?,
    userData: User,
    setUserChanged: (Boolean) -> Unit
) {
    val userFormViewModel: UserFormViewModel = koinViewModel {
        parametersOf(
            userData.nickname,
            userData.walkingGoal.toString(),
            userData.avatarIndex
        )
    }

    val uiState by userFormViewModel.uiState.collectAsState()

    var showRecommendation by remember { mutableStateOf(false) }

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
                    Text(text = "Dados", style = MaterialTheme.typography.titleMedium)

                    CustomOutlinedTextField(
                        value = uiState.nickname,
                        onValueChange = { userFormViewModel.updateNickname(it) },
                        label = "Apelido",
                        isError = uiState.errorNickname != null,
                        errorMessage = uiState.errorNickname,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NumberTextField(
                            value = uiState.walkingGoal,
                            onValueChange = { userFormViewModel.updateWalkingGoal(it) },
                            label = "Meta (min/semana)",
                            isError = uiState.errorWalkingGoal != null,
                            errorMessage = uiState.errorWalkingGoal,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = {
                                showRecommendation = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box {
                        Image(
                            painter = painterResource(id = avatarOptions[uiState.avatarIndex]),
                            contentDescription = "Body"
                        )
                    }

                    AvatarControlRow(
                        "Avatar",
                        avatarOptions.size,
                        uiState.avatarIndex,
                        { userFormViewModel.updateAvatarIndex((uiState.avatarIndex - 1 + avatarOptions.size) % avatarOptions.size) },
                        { userFormViewModel.updateAvatarIndex((uiState.avatarIndex + 1) % avatarOptions.size) }
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Button(
                        onClick = {
                            userFormViewModel.onSubmit(authUser!!.uid, setUserChanged, navController)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Salvar")
                        }
                    }
                }
            }
        }
        ErrorSnackbar(
            errorMessage = uiState.errorSubmit,
            onDismiss = {
                userFormViewModel.clearErrorSubmit()
            }
        )

        RecommendationDialog(showRecommendation) { showRecommendation = false }
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