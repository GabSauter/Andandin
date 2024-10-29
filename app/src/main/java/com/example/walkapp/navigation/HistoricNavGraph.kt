package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.views.badgesscreen.BadgesScreen
import com.example.walkapp.views.performancescreen.PerformanceScreen

@Composable
fun HistoricNavGraph(
    authUserId: String,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        route = Graph.Performance.route,
        startDestination = Screen.Performance.route
    ) {
        composable(Screen.Performance.route) {
            PerformanceScreen(authUserId)
        }
        composable(Screen.Badges.route) {
            BadgesScreen(authUserId)
        }
    }
}