package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.views.AvatarMakerScreen
import com.example.walkapp.views.UserFormScreen
import com.example.walkapp.views.WalkScreen
import com.google.firebase.auth.FirebaseUser

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    authUser: FirebaseUser?,
    onSignOut: () -> Unit,
) {
    NavHost(
        navController = navController,
        route = Graph.Home.route,
        startDestination = Screen.Walk.route
    ) {
        composable(Screen.Walk.route) {
            WalkScreen(
                navController = navController,
                authUser = authUser,
                onSignOut = { onSignOut() }
            )
        }
        composable(Screen.UserForm.route) {
            UserFormScreen(
                navController = navController,
                authUser = authUser,
            )
        }
        composable(Screen.AvatarMaker.route) {
            AvatarMakerScreen(
                navController = navController,
                authUser = authUser,
                passedAvatarIndex = null
            )
        }
    }
}