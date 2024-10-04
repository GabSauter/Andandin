package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.R
import com.example.walkapp.views.avatarmakerscreen.AvatarMakerScreen
import com.example.walkapp.views.badgesscreen.Badge
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
            if (authUser != null) {
                PerformanceScreen(authUser.uid)
            }
        }
        composable(Screen.Badges.route) {
            BadgesScreen(
                listOf(
                    Badge(imageRes = R.drawable.medalha1, description = "Achieved by walking 5km in total", isUnlocked = true),
                    Badge(imageRes = R.drawable.medalha2, description = "Achieved by walking 10km in total", isUnlocked = false),
                    Badge(imageRes = R.drawable.medalha3, description = "Achieved by walking 15km in total", isUnlocked = false),
                    Badge(imageRes = R.drawable.medalha4, description = "Achieved by walking 20km in total", isUnlocked = false),
                    Badge(imageRes = R.drawable.medalha5, description = "Achieved by walking 30km in total", isUnlocked = false),
                )
            )
        }
    }
}