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
import com.example.walkapp.navigation.HistoricNavGraph
import com.example.walkapp.navigation.Screen
import com.google.firebase.auth.FirebaseUser

@Composable
fun RootHistoricScreen(navController: NavHostController = rememberNavController(), authUser: FirebaseUser?){
    val tabItems = listOf(TabNavItems.Historic, TabNavItems.Performance, TabNavItems.Badges)
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
            HistoricNavGraph(navController = navController, authUser = authUser)
        }
    }
}

sealed class TabNavItems(
    val route: String,
    val title: String
) {
    data object Historic : TabNavItems(
        route = Screen.Historic.route,
        title = "Histórico"
    )

    data object Performance : TabNavItems(
        route = Screen.Performance.route,
        title = "Performance"
    )

    data object Badges : TabNavItems(
        route = Screen.Badges.route,
        title = "Conquistas"
    )
}