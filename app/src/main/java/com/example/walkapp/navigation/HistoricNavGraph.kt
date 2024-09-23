package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.views.avatarmakerscreen.AvatarMakerScreen
import com.example.walkapp.views.badgesscreen.BadgesScreen
import com.example.walkapp.views.historicscreen.HistoricScreen
import com.example.walkapp.views.peoplescreen.PeopleScreen
import com.example.walkapp.views.performancescreen.PerformanceScreen
import com.example.walkapp.views.userformscreen.UserFormScreen
import com.example.walkapp.views.walkscreen.WalkScreen
import com.google.firebase.auth.FirebaseUser

@Composable
fun HistoricNavGraph(
    authUser: FirebaseUser?,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        route = Graph.Historic.route,
        startDestination = Screen.Historic.route
    ) {
        composable(Screen.Historic.route) {
            HistoricScreen(authUser)
        }
        composable(Screen.Performance.route) {
            PerformanceScreen()
        }
        composable(Screen.Badges.route) {
            BadgesScreen()
        }
    }
}