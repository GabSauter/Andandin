package com.example.walkapp.views.peoplescreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.models.User
import com.example.walkapp.navigation.PeopleNavGraph
import com.google.firebase.auth.FirebaseUser

@Composable
fun PeopleScreen(
    authUser: FirebaseUser?,
    userData: User,
    navController: NavHostController = rememberNavController()
) {
//        if (no internet) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(MaterialTheme.colorScheme.tertiaryContainer)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Warning,
//                    contentDescription = "Sem conexão com a internet",
//                    tint = MaterialTheme.colorScheme.error
//                )
//                Text(
//                    text = "Sem conexão com a internet",
//                    color = MaterialTheme.colorScheme.onTertiaryContainer
//                )
//            }
//
//        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                PeopleNavGraph(
                    navController = navController,
                    authUser = authUser,
                    userData = userData
                )
//            }
        }
}