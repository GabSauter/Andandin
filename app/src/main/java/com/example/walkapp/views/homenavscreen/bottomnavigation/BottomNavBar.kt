package com.example.walkapp.views.homenavscreen.bottomnavigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItems) -> Unit
) {
    val items = listOf(
        BottomNavItems.Story,
        BottomNavItems.Performance,
        BottomNavItems.Walk,
        BottomNavItems.People
    )

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(item.icon), contentDescription = item.title) },
                label = { Text(item.title, fontSize = 11.5.sp) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == item.route,
                onClick = { onItemClick(item) },
            )
        }
    }
}