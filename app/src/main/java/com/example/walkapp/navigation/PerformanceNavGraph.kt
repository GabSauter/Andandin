package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.models.Badges
import com.example.walkapp.viewmodels.PerformanceUiState
import com.example.walkapp.views.badgesscreen.BadgesScreen
import com.example.walkapp.views.performancescreen.PerformanceScreen

@Composable
fun PerformanceNavGraph(
    authUserId: String,
    navController: NavHostController,
    performanceUiState: PerformanceUiState,
    error: String?,
    loading: Boolean,
    getLast7Days: () -> List<String>,
    getLast12Months: () -> List<String>,
    loadPerformanceData: (String) -> Unit,
    needToLoadPerformance: Boolean,
    badges: Badges?,
    loadingBadges: Boolean,
    errorBadges: String?,
    getBadges: (String) -> Unit,
    needToLoadBadges: Boolean
) {
    NavHost(
        navController = navController,
        route = Graph.Performance.route,
        startDestination = Screen.Performance.route
    ) {
        composable(Screen.Performance.route) {
            PerformanceScreen(
                authUserId,
                performanceUiState,
                error,
                loading,
                getLast7Days,
                getLast12Months,
                loadPerformanceData,
                needToLoadPerformance
            )
        }
        composable(Screen.Badges.route) {
            BadgesScreen(authUserId, badges, loadingBadges, errorBadges, getBadges, needToLoadBadges)
        }
    }
}