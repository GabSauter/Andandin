package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.models.Story
import com.example.walkapp.models.User
import com.example.walkapp.views.avatarmakerscreen.AvatarMakerScreen
import com.example.walkapp.views.storyscreen.StoryListScreen
import com.example.walkapp.views.peoplescreen.PeopleScreen
import com.example.walkapp.views.roothistoricscreen.RootHistoricScreen
import com.example.walkapp.views.storyscreen.StoryDetailScreen
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
                onSignOut = onSignOut
            )
        }
        composable(Graph.Historic.route) {
            RootHistoricScreen(authUser = authUser)
        }
        composable(Screen.People.route) {
            PeopleScreen(authUser, User(id = authUser?.uid.toString(), nickname = authUser?.displayName.toString(), avatarIndex = 0))
        }
        composable(Screen.UserForm.route) {
            UserFormScreen(navController, authUser)
        }
        composable(Screen.AvatarMaker.route) {
            AvatarMakerScreen(navController, authUser, 0)
        }
        composable(Screen.StoryList.route) {
            val stories = listOf(
                Story("Story 1", "This is the text of story 1", 1),
                Story("Story 2", "This is the text of story 2", 3),
                Story("Story 3", "This is the text of story 3", 5),
                Story("Story 4", "This is the text of story 4", 7)
            )
            val currentLevel = 4
            StoryListScreen(stories = stories, currentLevel = currentLevel, navController = navController)
        }
        composable("storyDetail/{title}/{text}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val text = backStackEntry.arguments?.getString("text") ?: ""
            StoryDetailScreen(title = title, text = text, navController = navController)
        }
    }
}