package com.example.walkapp.views.homescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.navigation.Graph
import com.example.walkapp.navigation.HomeNavGraph
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.HomeViewModel
import com.example.walkapp.viewmodels.WalkViewModel
import com.example.walkapp.views.homescreen.bottomnavigation.BottomNavBar
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    authUser: FirebaseUser?,
    onSignOut: () -> Unit
) {
    val showBottomBar = shouldShowBottomBar(navController)
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    onItemClick = {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HomeNavGraph(
                navController = navController,
                authUser = authUser,
                onSignOut = onSignOut
            )
        }
    }
}

@Composable
private fun shouldShowBottomBar(
    navController: NavHostController
): Boolean {
    val bottomBarScreens = listOf(
        Graph.Historic.route,
        Screen.Walk.route,
        Screen.People.route
    )
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    return bottomBarScreens.any { it == currentDestination?.route }
}