package com.example.walkapp.views.walkscreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HamburgerMenuButton(
    onEditClick: () -> Unit,
    onSignOut: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        IconButton(
            onClick = { expanded = true },
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onEditClick()
                },
                text = { Text("Editar dados") }
            )
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onSignOut()
                },
                text = { Text("Logout") }
            )
        }
    }
}