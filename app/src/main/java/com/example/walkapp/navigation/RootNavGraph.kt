package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.views.homescreen.HomeScreen
import com.example.walkapp.views.loginscreen.LoginScreen
import com.example.walkapp.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val authUser by authViewModel.user.collectAsState()
    val loading by authViewModel.loading.collectAsState()
    val errorMessage by authViewModel.error.collectAsState()

    AuthNavigation(navController = navController, user = authUser)

    NavHost(
        navController = navController,
        route = Graph.Root.route,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                signInWithGoogle = { authViewModel.signInWithGoogle(it) },
                loading = loading,
                errorMessage = errorMessage,
                clearErrorMessage = { authViewModel.clearError() }
            )
        }

        composable(Graph.Home.route) {
            HomeScreen(
                authUser = authUser,
                onSignOut = { authViewModel.signOut() }
            )
        }
    }
}

@Composable
fun AuthNavigation(
    navController: NavHostController,
    user: FirebaseUser?
) {
    LaunchedEffect(user) {
        if (user == null && navController.currentDestination?.route != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Graph.Root.route) { inclusive = true }
            }
        } else if (user != null && navController.currentDestination?.route == Screen.Login.route) {
            navController.navigate(Graph.Home.route) {
                popUpTo(Graph.Root.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}