package com.example.walkapp.views.groupscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

data class User(
    val avatar: Painter,
    val name: String,
    val distanceWalked: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    groupName: String,
    users: List<User>,
    onLeaveGroup: () -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }
    var showUserList by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = groupName,
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
                                onLeaveGroup()
                                showDropdown = false
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showUserList) {
            UserListDialog(users = users, onDismiss = { showUserList = false })
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(users) { user ->
                UserCard(user = user)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // User avatar (Replace with real image loading logic)
            Image(
                painter = user.avatar,
                contentDescription = "${user.name}'s Avatar",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            // User info
            Column {
                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Walked: ${user.distanceWalked}")
            }
        }
    }
}

@Composable
fun UserListDialog(users: List<User>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Group Members") },
        text = {
            Column {
                users.forEach { user ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = user.avatar,
                            contentDescription = "${user.name}'s Avatar",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = user.name)
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
    val dummyUsers = listOf(
        User(avatar = painterResource(android.R.drawable.ic_menu_camera), name = "John Doe", distanceWalked = "10 km"),
        User(avatar = painterResource(android.R.drawable.ic_menu_camera), name = "Jane Smith", distanceWalked = "15 km")
    )

    GroupScreen(
        groupName = "Morning Walkers",
        users = dummyUsers,
        onLeaveGroup = {}
    )
}