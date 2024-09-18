package com.example.walkapp.views.userformscreen.components

import android.app.DatePickerDialog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Locale

@Composable
fun DateTextField(
    date: String,
    onDateChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val initialYear = calendar.get(Calendar.YEAR)
    val initialMonth = calendar.get(Calendar.MONTH)
    val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate =
                    String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year)
                onDateChange(formattedDate)
            },
            initialYear, initialMonth, initialDay
        )
    }

    val datePickerInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(datePickerInteractionSource) {
        datePickerInteractionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                datePickerDialog.show()
            }
        }
    }

    OutlinedTextField(
        value = date,
        onValueChange = {},
        label = { Text(label) },
        modifier = modifier,
        interactionSource = datePickerInteractionSource,
        isError = isError,
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
            }
        },
        readOnly = true
    )

    if (isError) {
        Text(
            text = errorMessage ?: "",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth()
        )
    }
}