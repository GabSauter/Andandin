package com.example.walkapp.navigation

sealed class Graph(val route: String) {
    data object Root: Graph(route = "root_graph")
    data object Home: Screen(route = "home_graph")
}

sealed class Screen(val route: String) {
    data object Login: Screen(route = "login_screen")

    data object Walk: Screen(route = "walk_screen")
    data object Historic: Screen(route = "historic_screen")
    data object People: Screen(route = "people_screen")

    data object Loading: Screen(route = "loading_screen")
    data object UserForm: Screen(route = "userForm_screen")
    data object AvatarMaker: Screen(route = "avatarMaker_screen")
}