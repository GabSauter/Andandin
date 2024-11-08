package com.example.walkapp.views.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ErrorSnackbar(
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        showError = errorMessage != null
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = showError,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Snackbar(
                action = {
                    TextButton(onClick = { onDismiss() }) {
                        Text(
                            text = "Fechar", color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                },
                containerColor = Color.Red
            ) {
                Text(text = errorMessage.orEmpty(), color = MaterialTheme.colorScheme.onError)
            }
        }

        LaunchedEffect(errorMessage) {
            kotlinx.coroutines.delay(5000)
            onDismiss()
            showError = false
        }
    }
}

