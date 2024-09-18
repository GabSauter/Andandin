package com.example.walkapp.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walkapp.R
import com.example.walkapp.helpers.CredentialHelper
import com.example.walkapp.views.components.ErrorSnackbar

@Composable
fun LoginScreen(
    signInWithGoogle: (credentialHelper: CredentialHelper) -> Unit,
    loading: Boolean,
    errorMessage: String?,
    clearErrorMessage: () -> Unit
) {
    val context = LocalContext.current
    val credentialHelper = remember {
        CredentialHelper(
            context = context,
            credentialManager = androidx.credentials.CredentialManager.create(context)
        )
    }

    Box {
        Image(
            painter = painterResource(id = R.drawable.img_forest_background),
            contentDescription = "Imagem de floresta",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Andandin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aventure-se caminhando!",
                style = MaterialTheme.typography.titleSmall,
            )

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    if (!loading) {
                        signInWithGoogle(credentialHelper)
                    }
                }
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Login com o Google")
                    }
                }
            }
        }
        ErrorSnackbar(
            errorMessage = errorMessage,
            onDismiss = {
                clearErrorMessage()
            }
        )
    }
}