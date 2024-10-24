package com.example.walkapp.views.leaderboardscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.walkapp.R
import com.example.walkapp.common.avatarOptions
import com.example.walkapp.models.LeaderboardUser
import com.example.walkapp.viewmodels.LeaderboardViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LeaderboardScreen(userId: String) {
    var selectedFilter by remember { mutableIntStateOf(0) }

    val leaderboardViewModel = koinViewModel<LeaderboardViewModel>()
    val leaderboard by leaderboardViewModel.leaderboard.collectAsState()
    //val leaderboardInGroup by leaderboardViewModel.leaderboardInGroup.collectAsState()
    val userLeaderboard by leaderboardViewModel.user.collectAsState()
    val error by leaderboardViewModel.error.collectAsState()

    //val currentLeaderboard = if (selectedFilter == 0) leaderboard else leaderboardInGroup
    val currentLeaderboard = leaderboard

    LaunchedEffect(selectedFilter) {
        if (selectedFilter == 0) {
            leaderboardViewModel.getLeaderboardForMonth()
        } else {
            //leaderboardViewModel.getLeaderboardForMonthInGroup("10/2024", "your-group-id")
        }
    }

    LaunchedEffect(userLeaderboard) {
        leaderboardViewModel.getUserLeaderboard(userId)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TopButtons(
                            selectedIndex = selectedFilter,
                            onSelectionChanged = { selectedFilter = it }
                        )
                    }
                }
                items(currentLeaderboard) { userLeaderboard ->
                    LeaderboardItem(userLeaderboard)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(color = MaterialTheme.colorScheme.primaryContainer))
            {
                LeaderboardItem(
                    leaderboardUser = userLeaderboard,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun TopButtons(selectedIndex: Int, onSelectionChanged: (Int) -> Unit) {
    val options = listOf("Global", "Grupo")
    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onSelectionChanged(index) },
                selected = index == selectedIndex
            ) {
                Text(label)
            }
        }
    }
}

@Composable
fun LeaderboardItem(leaderboardUser: LeaderboardUser, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = avatarOptions[leaderboardUser.avatarIndex]),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(36.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = leaderboardUser.nickname,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${leaderboardUser.distance} km",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
fun LeaderboardPreview() {
    MaterialTheme {
        LeaderboardScreen("userId")
    }
}