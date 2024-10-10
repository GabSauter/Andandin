package com.example.walkapp.views.storyscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.walkapp.models.Story
import com.example.walkapp.navigation.Screen

@Composable
fun StoryListScreen(stories: List<Story>, currentLevel: Int, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .weight(.4f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(modifier = Modifier.weight(.6f))

            Text(
                text = "Histórias",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(stories) { story ->
                StoryCard(story = story, currentLevel = currentLevel) {
                    if (currentLevel >= story.requiredLevel) {
                        navController.navigate(Screen.StoryDetail.createRoute(story.title, story.text))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
fun StoryCard(story: Story, currentLevel: Int, onClick: () -> Unit) {
    val isLocked = currentLevel < story.requiredLevel

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked) { onClick() }
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (isLocked) {
                    Text(
                        text = "Desbloqueia no nível ${story.requiredLevel}",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = "Desbloqueado",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StoryListScreenPreview(){
    val stories = listOf(
        Story("Story 1", "This is the text of story 1", 1),
        Story("Story 2", "This is the text of story 2", 3),
        Story("Story 3", "This is the text of story 3", 5),
        Story("Story 4", "This is the text of story 4", 7)
    )
    val currentLevel = 4
    StoryListScreen(stories = stories, currentLevel = currentLevel, navController = rememberNavController())
}