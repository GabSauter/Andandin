package com.example.walkapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.walkapp.models.Level
import com.example.walkapp.models.Story
import com.example.walkapp.models.User
import com.example.walkapp.viewmodels.HistoricViewModel
import com.example.walkapp.views.avatarmakerscreen.AvatarMakerScreen
import com.example.walkapp.views.historicscreen.HistoricScreen
import com.example.walkapp.views.storyscreen.StoryListScreen
import com.example.walkapp.views.peoplescreen.PeopleScreen
import com.example.walkapp.views.roothistoricscreen.RootHistoricScreen
import com.example.walkapp.views.storyscreen.StoryDetailScreen
import com.example.walkapp.views.userformscreen.UserFormScreen
import com.example.walkapp.views.walkscreen.WalkScreen
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    authUser: FirebaseUser?,
    onSignOut: () -> Unit,
    userData: User,
    setUserChanged: (Boolean) -> Unit,
    level: Level,
) {
    val historicViewModel = koinViewModel<HistoricViewModel>()
    val walkHistoric by historicViewModel.walkHistory.collectAsState()
    val needToLoadHistoric by historicViewModel.needToLoadHistoric.collectAsState()
    val isFetching by historicViewModel.isFetching.collectAsState()
    val isEndReached by historicViewModel.isEndReached.collectAsState()

    NavHost(
        navController = navController,
        route = Graph.Home.route,
        startDestination = Screen.Walk.route
    ) {
        composable(Screen.Walk.route) {
            if (authUser != null) {
                WalkScreen(
                    userData = userData,
                    level = level,
                    navController = navController,
                    onSignOut = onSignOut
                )
            }
        }
        composable(Graph.Historic.route) {
            if (authUser != null) {
                RootHistoricScreen(authUserId = authUser.uid)
            }
        }
        composable(Screen.Historic.route) {
            if (authUser != null) {
                HistoricScreen(
                    authUserId = authUser.uid,
                    navController = navController,
                    walkHistoric = walkHistoric,
                    needToLoadHistoric = needToLoadHistoric,
                    loadWalkHistory = { historicViewModel.loadWalkHistory(authUser.uid) },
                    setNeedToLoadHistoric = { historicViewModel.setNeedToLoadHistoric(false) },
                    isEndReached = isEndReached,
                    isFetching = isFetching
                )
            }
        }
        composable(Screen.People.route) {
            if (authUser != null) {
                PeopleScreen(
                    authUser,
                    User(
                        id = authUser.uid,
                        nickname = userData.nickname,
                        avatarIndex = userData.avatarIndex
                    )
                )
            }
        }
        composable(Screen.UserForm.route) {
            UserFormScreen(navController, authUser, userData, setUserChanged)
        }
        composable(Screen.AvatarMaker.route) {
            AvatarMakerScreen(navController, authUser, userData.avatarIndex)
        }
        composable(Screen.StoryList.route) {
            val stories = listOf(
                Story("Story 1", "This is the text of story 1", 1),
                Story("Story 2", "This is the text of story 2", 3),
                Story("Story 3", "This is the text of story 3", 5),
                Story("Story 4", "This is the text of story 4", 7)
            )
            StoryListScreen(
                stories = stories,
                currentLevel = level.level,
                navController = navController
            )
        }
        composable("storyDetail/{title}/{text}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val text = backStackEntry.arguments?.getString("text") ?: ""
            StoryDetailScreen(title = title, text = text, navController = navController)
        }
    }
}