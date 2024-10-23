package com.example.walkapp.views.peoplescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.models.User
import com.example.walkapp.navigation.PeopleNavGraph
import com.example.walkapp.navigation.Screen
import com.google.firebase.auth.FirebaseUser

@Composable
fun PeopleScreen(
    authUser: FirebaseUser?,
    userData: User,
    navController: NavHostController = rememberNavController()
) {
    val tabItems = listOf(TabNavItems.Leaderboard, TabNavItems.EnterGroup)
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

//        if (no internet) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(MaterialTheme.colorScheme.tertiaryContainer)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Warning,
//                    contentDescription = "Sem conexão com a internet",
//                    tint = MaterialTheme.colorScheme.error
//                )
//                Text(
//                    text = "Sem conexão com a internet",
//                    color = MaterialTheme.colorScheme.onTertiaryContainer
//                )
//            }
//
//        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                PeopleNavGraph(
                    navController = navController,
                    authUser = authUser,
                    userData = userData
                )
//            }
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

    data object EnterGroup : TabNavItems(
        route = Screen.EnterGroup.route,
        title = "Grupo"
    )
}