package com.example.walkapp.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.walkapp.R
import com.example.walkapp.views.components.ErrorSnackbar
import com.example.walkapp.ui.theme.WalkAppTheme
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(signInWithGoogle: () -> Unit, loading: Boolean, errorMessage: String?, clearErrorMessage: () -> Unit) {
    val titleVisible = remember { mutableStateOf(false) }
    val subtitleVisible = remember { mutableStateOf(false) }

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
            LaunchedEffect(Unit) {
                titleVisible.value = true
                delay(1000)
                subtitleVisible.value = true
            }

            AnimatedVisibility(visible = titleVisible.value) {
                Text(
                    text = "Andandin",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alpha(
                        animateFloatAsState(
                            targetValue = if (titleVisible.value) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = LinearOutSlowInEasing
                            ),
                            label = "Fade In"
                        ).value
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = subtitleVisible.value) {
                Text(
                    text = "Aventure-se caminhando!",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.alpha(
                        animateFloatAsState(
                            targetValue = if (subtitleVisible.value) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = LinearOutSlowInEasing
                            ),
                            label = "Fade In"
                        ).value
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val buttonOffset = animateDpAsState(
                targetValue = if (subtitleVisible.value) 0.dp else 60.dp,
                animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
                label = "Fade In"
            )

            Button(
                onClick = {
                    if (!loading) {
                        signInWithGoogle()
                    }
                },
                modifier = Modifier
                    .offset(y = buttonOffset.value)
                    .animateContentSize()
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

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    WalkAppTheme {
        //LoginScreen()
    }
}