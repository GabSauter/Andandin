package com.example.walkapp.views.homescreen.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.walkapp.navigation.Screen

sealed class BottomNavItems(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Historic : BottomNavItems(
        route = Screen.Historic.route,
        title = "Histórico",
        icon = Icons.Default.DateRange
    )

    data object Walk : BottomNavItems(
        route = Screen.Walk.route,
        title = "Caminhar",
        icon = Icons.Default.PlayArrow
    )

    data object People : BottomNavItems(
        route = Screen.People.route,
        title = "Pessoas",
        icon = Icons.Default.Person
    )
}