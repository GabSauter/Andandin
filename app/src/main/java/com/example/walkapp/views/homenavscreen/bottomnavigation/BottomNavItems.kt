package com.example.walkapp.views.homenavscreen.bottomnavigation

import com.example.walkapp.R
import com.example.walkapp.navigation.Graph
import com.example.walkapp.navigation.Screen

sealed class BottomNavItems(
    val route: String,
    val title: String,
    val icon: Int
) {
    data object Historic : BottomNavItems(
        route = Graph.Historic.route,
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