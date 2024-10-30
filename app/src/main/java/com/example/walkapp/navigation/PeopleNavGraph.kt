package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.views.entergroupscreen.EnterGroupScreen
import com.example.walkapp.views.groupscreen.GroupScreen
import com.google.firebase.auth.FirebaseUser

@Composable
fun PeopleNavGraph(
    authUser: FirebaseUser?,
    navController: NavHostController,
    userData: com.example.walkapp.models.User
) {
    NavHost(
        navController = navController,
        route = Graph.People.route,
        startDestination = Screen.EnterGroup.route
    ) {
        composable(Screen.EnterGroup.route) {
            if (authUser != null) {
                EnterGroupScreen(navController, authUser.uid, userData)
            }
        }
        composable(Screen.Group.route) {
            if (authUser != null) {
                GroupScreen(authUser.uid, navController)
            }
        }
    }
}