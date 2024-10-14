package com.example.walkapp.views.peoplescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.walkapp.views.leaderboardscreen.LeaderboardView

@Composable
fun PeopleScreen() {
    Box {
        Text(text = "People Screen")
    }
    LeaderboardView()
}