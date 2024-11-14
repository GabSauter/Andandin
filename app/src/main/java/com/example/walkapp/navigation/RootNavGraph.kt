package com.example.walkapp.navigation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.views.homenavscreen.HomeNavScreen
import com.example.walkapp.views.loginscreen.LoginScreen
import com.example.walkapp.viewmodels.AuthViewModel
import com.example.walkapp.views.WelcomeScreen
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@Composable
fun RootNavGraph(
    navController: NavHostController,
    context: Context
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val authUser by authViewModel.user.collectAsState()
    val loading by authViewModel.loading.collectAsState()
    val errorMessage by authViewModel.error.collectAsState()

    val preferences = context.getSharedPreferences("walkapp_preferences", Context.MODE_PRIVATE)
    val isFirstTimeUser = preferences.getBoolean("is_first_time_user", true)

    AuthNavigation(navController = navController, user = authUser, isFirstTimeUser)

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
            HomeNavScreen(
                authUser = authUser,
                onSignOut = { authViewModel.signOut() }
            )
        }

        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onContinue = {
                    preferences.edit().putBoolean("is_first_time_user", false).apply()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Graph.Root.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun AuthNavigation(
    navController: NavHostController,
    user: FirebaseUser?,
    isFirstTimeUser: Boolean
) {
    LaunchedEffect(user) {
        if (isFirstTimeUser) {
            navController.navigate(Screen.Welcome.route)
        } else if (user == null && navController.currentDestination?.route != Screen.Login.route) {
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