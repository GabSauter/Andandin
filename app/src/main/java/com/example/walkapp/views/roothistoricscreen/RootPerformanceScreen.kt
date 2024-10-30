package com.example.walkapp.views.roothistoricscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
    needToLoadPerformance: Boolean
) {
    val tabItems = listOf(TabNavItems.Performance, TabNavItems.Badges)
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
                        selectedTabIndex = index
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    },
                    text = { Text(screen.title) }
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
                needToLoadPerformance = needToLoadPerformance
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

    data object Badges : TabNavItems(
        route = Screen.Badges.route,
        title = "Conquistas"
    )
}