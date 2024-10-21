package com.example.walkapp.views.entergroupscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.walkapp.navigation.Screen
import com.example.walkapp.viewmodels.EnterGroupViewModel
import com.example.walkapp.views.components.ErrorSnackbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun EnterGroupScreen(
    navController: NavHostController,
    userId: String,
    userData: com.example.walkapp.models.User,
) {
    val enterGroupViewModel = koinViewModel<EnterGroupViewModel>()
    val userPartOfGroup by enterGroupViewModel.userPartOfGroup.collectAsState()
    val loadingJoinOrCreateGroup by enterGroupViewModel.loadingJoinOrCreateGroup.collectAsState()
    val loadingUserPartOfGroup by enterGroupViewModel.loadingUserPartOfGroup.collectAsState()
    val error by enterGroupViewModel.error.collectAsState()
    val groupName by enterGroupViewModel.groupName.collectAsState()
    val groupPassword by enterGroupViewModel.groupPassword.collectAsState()

    LaunchedEffect(userPartOfGroup){
        enterGroupViewModel.isUserPartOfGroup(userId)
        if(userPartOfGroup){
            navController.navigate(Screen.Group.route)
        }
    }

    if(loadingUserPartOfGroup){
        Box(Modifier.fillMaxSize()){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }else{
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { enterGroupViewModel.updateGroupName(it) },
                    label = { Text("Nome do grupo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = groupPassword,
                    onValueChange = { enterGroupViewModel.updateGroupPassword(it) },
                    label = { Text("Senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if(!loadingJoinOrCreateGroup){
                            enterGroupViewModel.joinGroup(groupName, groupPassword, userId, userData = userData)
                            if(userPartOfGroup){
                                navController.navigate(Screen.Group.route)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(loadingJoinOrCreateGroup) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    }else{
                        Text("Entrar no Grupo")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        if(!loadingJoinOrCreateGroup){
                            enterGroupViewModel.createGroup(
                                groupName,
                                groupPassword,
                                userId,
                                userData = userData
                            )
                            if(userPartOfGroup){
                                navController.navigate(Screen.Group.route)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(loadingJoinOrCreateGroup) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }else {
                        Text("Criar Grupo")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Entrar em um grupo que caminhe te dará mais XP!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    ErrorSnackbar(
        errorMessage = error,
        onDismiss = {
            enterGroupViewModel.clearError()
        }
    )
}