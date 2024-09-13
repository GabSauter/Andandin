package com.example.walkapp.views.components.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.walkapp.navigation.Screen

sealed class BottomNavItems(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItems(
        route = Screen.Walk.route,
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Profile : BottomNavItems(
        route = Screen.UserForm.route,
        title = "Profile",
        icon = Icons.Default.Person
    )

    data object Settings : BottomNavItems(
        route = Screen.AvatarMaker.route,
        title = "Settings",
        icon = Icons.Default.Settings
    )
}