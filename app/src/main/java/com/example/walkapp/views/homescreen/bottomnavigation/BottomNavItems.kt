package com.example.walkapp.views.homescreen.bottomnavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.walkapp.R
import com.example.walkapp.navigation.Screen

sealed class BottomNavItems(
    val route: String,
    val title: String,
    val icon: Int
) {
    data object Historic : BottomNavItems(
        route = Screen.Historic.route,
        title = "Histórico",
        icon = R.drawable.ic_book
    )

    data object Walk : BottomNavItems(
        route = Screen.Walk.route,
        title = "Caminhar",
        icon = R.drawable.ic_walk
    )

    data object People : BottomNavItems(
        route = Screen.People.route,
        title = "Pessoas",
        icon = R.drawable.ic_people
    )
}