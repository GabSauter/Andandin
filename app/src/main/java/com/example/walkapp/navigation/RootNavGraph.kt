package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.helpers.CredentialHelper
import com.example.walkapp.views.homescreen.HomeScreen
import com.example.walkapp.views.LoginScreen
import com.example.walkapp.viewmodels.AuthViewModel
import com.example.walkapp.views.AvatarMakerScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    val authViewModel = koinViewModel<AuthViewModel>()

    val context = LocalContext.current
    val credentialHelper = remember {
        CredentialHelper(
            context = context,
            credentialManager = CredentialManager.create(context)
        )
    }

    val authUser by authViewModel.user.collectAsState()

    LaunchedEffect(authUser) {
        if (authUser == null && navController.currentDestination?.route != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        } else if (authUser != null && navController.currentDestination?.route == Screen.Login.route) {
            navController.navigate(Graph.Home.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        route = Graph.Root.route,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            val loading by authViewModel.loading.collectAsState()
            val errorMessage by authViewModel.error.collectAsState()
            LoginScreen(
                signInWithGoogle = { authViewModel.signInWithGoogle(credentialHelper) },
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

        composable(Screen.AvatarMaker.route) {
            AvatarMakerScreen(
                navController = navController,
                authUser = authUser,
                passedAvatarIndex = 0
            )
        }
    }
}