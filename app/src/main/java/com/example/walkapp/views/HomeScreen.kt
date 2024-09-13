package com.example.walkapp.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.navigation.HomeNavGraph
import com.example.walkapp.views.components.bottomnavigation.BottomNavBar
import com.google.firebase.auth.FirebaseUser

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    authUser: FirebaseUser?,
    onSignOut: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                onItemClick = { navController.navigate(it.route) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            HomeNavGraph(
                navController = navController,
                authUser = authUser,
                onSignOut = onSignOut)
        }
    }
}