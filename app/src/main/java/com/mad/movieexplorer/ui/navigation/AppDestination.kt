package com.mad.movieexplorer.ui.navigation

sealed class AppDestination(
    val route: String
) {
    data object Auth : AppDestination("auth")
    data object Home : AppDestination("home")
    data object Search : AppDestination("search")
    data object Favourites : AppDestination("favourites")
    data object Rentals : AppDestination("rentals")
    data object Profile : AppDestination("profile")
    data object Details : AppDestination("details/{movieId}") {
        fun createRoute(movieId: String): String = "details/$movieId"
    }
}
