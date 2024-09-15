package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.views.AvatarMakerScreen
import com.example.walkapp.views.HistoricScreen
import com.example.walkapp.views.PeopleScreen
import com.example.walkapp.views.userformscreen.UserFormScreen
import com.example.walkapp.views.walkscreen.WalkScreen
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
        composable(Screen.Historic.route) {
            HistoricScreen()
        }
        composable(Screen.People.route) {
            PeopleScreen()
        }

        composable(Screen.UserForm.route) {
            UserFormScreen(navController, authUser)
        }
        composable(Screen.AvatarMaker.route) {
            AvatarMakerScreen(navController, authUser, 0)
        }
    }
}