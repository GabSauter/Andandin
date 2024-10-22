package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.views.entergroupscreen.EnterGroupScreen
import com.example.walkapp.views.groupscreen.GroupScreen
import com.example.walkapp.views.leaderboardscreen.LeaderboardScreen
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
        startDestination = Screen.Leaderboard.route
    ) {
        composable(Screen.Leaderboard.route) {
            if (authUser != null) {
                LeaderboardScreen(authUser.uid)
            }
        }
        composable(Screen.EnterGroup.route) {
            if (authUser != null) {
                EnterGroupScreen(navController, authUser.uid, userData)
            }
        }
        composable(Screen.Group.route) {
            if (authUser != null) {
                GroupScreen(authUser.uid)
            }
        }
    }
}