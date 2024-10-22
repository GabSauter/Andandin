package com.example.walkapp.views.groupscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.walkapp.common.avatarOptions
import com.example.walkapp.models.GroupUser
import com.example.walkapp.models.GroupUserWalk
import com.example.walkapp.viewmodels.GroupViewModel
import com.example.walkapp.views.historicscreen.WalkHistoryCard
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(userId: String) {

    val groupViewModel: GroupViewModel = koinViewModel(parameters = {
        parametersOf(userId)
    })

    val group by groupViewModel.group.collectAsState()
    val groupUsers by groupViewModel.groupUsers.collectAsState()
    val groupUsersWalks by groupViewModel.groupUsersWalks.collectAsState()
    val userPartOfGroup by groupViewModel.userPartOfGroup.collectAsState()
    val loading by groupViewModel.loading.collectAsState()
    val error by groupViewModel.error.collectAsState()

    var showDropdown by remember { mutableStateOf(false) }
    var showUserList by remember { mutableStateOf(false) }

    if(loading || group == null){
        Box(modifier = Modifier.fillMaxSize()){
            CircularProgressIndicator()
        }
    }else{
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = group!!.name,
                            modifier = Modifier.clickable { showUserList = true }
                        )
                    },
                    actions = {
                        IconButton(onClick = { showDropdown = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Leave Group") },
                                onClick = {
                                    groupViewModel.leaveGroup(userId)
                                    showDropdown = false
                                }
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (showUserList) {
                UserListDialog(users = groupUsers, onDismiss = { showUserList = false })
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if(groupUsersWalks.isEmpty()){
                    item{
                        Text(
                            text = "Nenhuma caminhada encontrada.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }else{
                    items(groupUsersWalks.size) { index ->
                        UserCard(groupUsersWalks[index])

                        if (index == groupUsersWalks.size - 1) {
                            groupViewModel.loadUserWalks(userId)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(groupUserWalk: GroupUserWalk) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(avatarOptions[groupUserWalk.avatarIndex]),
                contentDescription = "${groupUserWalk.nickname}'s Avatar",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = groupUserWalk.nickname, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Caminhou: ${groupUserWalk.distanceWalked}m")
            }
        }
    }
}

@Composable
fun UserListDialog(users: List<GroupUser>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Group Members") },
        text = {
            Column {
                users.forEach { user ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(avatarOptions[user.avatarIndex]),
                            contentDescription = "${user.nickname}'s Avatar",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.nickname)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GroupScreenPreview() {
    GroupScreen("123")
}