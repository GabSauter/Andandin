package com.example.walkapp.views.rootperformancescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.models.Badges
import com.example.walkapp.navigation.PerformanceNavGraph
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.PerformanceUiState

@Composable
fun RootPerformanceScreen(
    navController: NavHostController = rememberNavController(),
    authUserId: String,
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
    val tabItems = listOf(TabNavItems.Performance, TabNavItems.Leaderboard, TabNavItems.Badges)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabItems.forEachIndexed { index, screen ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        if(selectedTabIndex != index){
                            selectedTabIndex = index
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    text = { Text(screen.title, fontSize = 13.sp) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            PerformanceNavGraph(
                navController = navController,
                authUserId = authUserId,
                performanceUiState = performanceUiState,
                error = error,
                loading = loading,
                getLast7Days = getLast7Days,
                getLast12Months = getLast12Months,
                loadPerformanceData = loadPerformanceData,
                needToLoadPerformance = needToLoadPerformance,
                badges = badges,
                loadingBadges = loadingBadges,
                errorBadges = errorBadges,
                getBadges = getBadges,
                needToLoadBadges = needToLoadBadges
            )
        }
    }
}

sealed class TabNavItems(
    val route: String,
    val title: String
) {
    data object Performance : TabNavItems(
        route = Screen.Performance.route,
        title = "Performance"
    )

    data object Leaderboard : TabNavItems(
        route = Screen.Leaderboard.route,
        title = "Ranking"
    )

    data object Badges : TabNavItems(
        route = Screen.Badges.route,
        title = "Conquistas"
    )
}