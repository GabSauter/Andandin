package com.example.walkapp.views.homenavscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.navigation.Graph
import com.example.walkapp.navigation.HomeNavGraph
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.HomeViewModel
import com.example.walkapp.views.homenavscreen.bottomnavigation.BottomNavBar
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeNavScreen(
    navController: NavHostController = rememberNavController(),
    authUser: FirebaseUser?,
    onSignOut: () -> Unit
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val loadingUserData by homeViewModel.loadingUserData.collectAsState()
    val userData by homeViewModel.user.collectAsState()
    val userDataChanged by homeViewModel.userChanged.collectAsState()
    val level by homeViewModel.level.collectAsState()
    val needToLoadXp by homeViewModel.needToLoadXp.collectAsState()
    val savingWalk by homeViewModel.savingWalk.collectAsState()

    LaunchedEffect(userDataChanged, needToLoadXp) {
        if(authUser != null && !loadingUserData){
            if (userData == null || userDataChanged || needToLoadXp) {
                homeViewModel.loadUserData(authUser.uid)
            }
        }
    }

    if (savingWalk || loadingUserData || userData == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }
    }else{
        val showBottomBar = shouldShowBottomBar(navController)
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavBar(
                        navController = navController,
                        onItemClick = {
                            navController.navigate(it.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HomeNavGraph(
                    navController = navController,
                    authUser = authUser,
                    onSignOut = onSignOut,
                    userData = userData!!,
                    setUserChanged = { homeViewModel.setUserChanged(it) },
                    level = level!!
                )
            }
        }
    }
}

@Composable
private fun shouldShowBottomBar(
    navController: NavHostController
): Boolean {
    val bottomBarScreens = listOf(
        Graph.Performance.route,
        Screen.Walk.route,
        Screen.People.route
    )
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    return bottomBarScreens.any { it == currentDestination?.route }
}