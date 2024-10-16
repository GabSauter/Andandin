package com.example.walkapp.views.peoplescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.navigation.PeopleNavGraph
import com.example.walkapp.navigation.Screen
import com.google.firebase.auth.FirebaseUser

@Composable
fun PeopleScreen(authUser: FirebaseUser?, navController: NavHostController = rememberNavController()) {
    val tabItems = listOf(TabNavItems.Leaderboard, TabNavItems.Group)
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
            PeopleNavGraph(navController = navController, authUser = authUser)
        }
    }
}

sealed class TabNavItems(
    val route: String,
    val title: String
) {
    data object Leaderboard : TabNavItems(
        route = Screen.Leaderboard.route,
        title = "Ranking"
    )

    data object Group : TabNavItems(
        route = Screen.Group.route,
        title = "Grupo"
    )
}