package com.example.walkapp.navigation

sealed class Graph(val route: String) {
    data object Root: Graph(route = "root_graph")
    data object Home: Graph(route = "home_graph")
    data object Performance: Graph(route = "performance_graph")
    data object People: Graph(route = "people_graph")
}

sealed class Screen(val route: String) {
    data object Login: Screen(route = "login_screen")
    data object Welcome: Screen(route = "welcome_screen")

    data object Walk: Screen(route = "walk_screen")
    data object Historic: Screen(route = "historic_screen")
    data object Performance: Screen(route = "performance_screen")
    data object Badges: Screen(route = "badges_screen")
    data object People: Screen(route = "people_screen")

    data object UserForm: Screen(route = "userForm_screen")

    data object StoryList: Screen(route = "storyList_screen")
    data object StoryDetail : Screen("storyDetail/{title}/{text}") {
        fun createRoute(title: String, text: String): String {
            return "storyDetail/$title/$text"
        }
    }

    data object EnterGroup: Screen(route = "enter_group_screen")
    data object Group: Screen(route = "group_screen")
    data object Leaderboard: Screen(route = "leaderboard_screen")
}