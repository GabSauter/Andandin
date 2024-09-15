package com.example.walkapp.views.homescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.navigation.HomeNavGraph
import com.example.walkapp.navigation.Screen
import com.example.walkapp.views.homescreen.bottomnavigation.BottomNavBar
import com.google.firebase.auth.FirebaseUser

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    authUser: FirebaseUser?,
    onSignOut: () -> Unit
) {
    val screens = listOf(
        Screen.Historic,
        Screen.Walk,
        Screen.People
    )
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    Scaffold(
        bottomBar = {
            if (bottomBarDestination) {
                    BottomNavBar(
                        navController = navController,
                        onItemClick = { navController.navigate(it.route) }
                    )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            HomeNavGraph(
                navController = navController,
                authUser = authUser,
                onSignOut = onSignOut
            )
        }
    }
}