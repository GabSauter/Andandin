package com.example.walkapp.views.groupscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.walkapp.navigation.Screen


@Composable
fun EnterGroupScreen(
    navController: NavHostController,
    userId: String,
) {
    var groupName =""
    var groupPassword = ""

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Nome do grupo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = groupPassword,
                onValueChange = { groupPassword = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /*onEnterGroup(groupName, groupPassword)*/
                    navController.navigate(Screen.Group.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar no Grupo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /*onCreateGroup()*/ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Grupo")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Entrar em um grupo que caminhe te dará mais XP!",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}