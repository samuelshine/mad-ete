package com.mad.movieexplorer.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mad.movieexplorer.AppContainer
import com.mad.movieexplorer.AppViewModelFactory
import com.mad.movieexplorer.ui.screens.auth.AuthScreen
import com.mad.movieexplorer.ui.screens.details.MovieDetailsScreen
import com.mad.movieexplorer.ui.screens.favourites.FavouritesScreen
import com.mad.movieexplorer.ui.screens.home.HomeScreen
import com.mad.movieexplorer.ui.screens.profile.ProfileScreen
import com.mad.movieexplorer.ui.screens.rentals.RentalsScreen
import com.mad.movieexplorer.ui.screens.search.SearchScreen
import com.mad.movieexplorer.ui.theme.DeepOcean
import com.mad.movieexplorer.ui.theme.Night
import com.mad.movieexplorer.ui.theme.SurfaceMid
import com.mad.movieexplorer.viewmodel.AuthViewModel
import com.mad.movieexplorer.viewmodel.FavouritesViewModel
import com.mad.movieexplorer.viewmodel.MovieViewModel
import com.mad.movieexplorer.viewmodel.RentalViewModel

private data class BottomNavItem(
    val destination: AppDestination,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieExplorerNavGraph(
    appContainer: AppContainer
) {
    val factory = remember(appContainer) { AppViewModelFactory(appContainer) }
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val movieViewModel: MovieViewModel = viewModel(factory = factory)
    val favouritesViewModel: FavouritesViewModel = viewModel(factory = factory)
    val rentalViewModel: RentalViewModel = viewModel(factory = factory)

    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val movieState by movieViewModel.uiState.collectAsStateWithLifecycle()
    val favouritesState by favouritesViewModel.uiState.collectAsStateWithLifecycle()
    val rentalState by rentalViewModel.uiState.collectAsStateWithLifecycle()

    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    val bottomNavItems = remember {
        listOf(
            BottomNavItem(AppDestination.Home, "Home", Icons.Outlined.Home),
            BottomNavItem(AppDestination.Search, "Search", Icons.Outlined.Search),
            BottomNavItem(AppDestination.Favourites, "Favourites", Icons.Outlined.FavoriteBorder),
            BottomNavItem(AppDestination.Rentals, "Rentals", Icons.Outlined.ShoppingBag),
            BottomNavItem(AppDestination.Profile, "Profile", Icons.Outlined.Person)
        )
    }

    val showTopBar = !authState.isLoadingSession &&
        currentRoute != null &&
        currentRoute != AppDestination.Auth.route
    val showBottomBar = showTopBar && currentRoute != AppDestination.Details.route

    LaunchedEffect(authState.isLoadingSession, authState.isAuthenticated, currentRoute) {
        if (authState.isLoadingSession) return@LaunchedEffect

        if (authState.isAuthenticated && currentRoute == AppDestination.Auth.route) {
            navController.navigate(AppDestination.Home.route) {
                popUpTo(AppDestination.Auth.route) { inclusive = true }
                launchSingleTop = true
            }
        } else if (!authState.isAuthenticated && currentRoute != AppDestination.Auth.route) {
            navController.navigate(AppDestination.Auth.route) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(rentalState.message) {
        rentalState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            rentalViewModel.clearMessage()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showTopBar) {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Movie Explorer") },
                    navigationIcon = {
                        if (currentRoute == AppDestination.Details.route) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.92f),
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.destination.route,
                            onClick = {
                                navController.navigate(item.destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(text = item.label) }
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Night, DeepOcean, SurfaceMid)
                    )
                )
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = AppDestination.Auth.route
            ) {
                composable(AppDestination.Auth.route) {
                    AuthScreen(
                        uiState = authState,
                        onEmailChange = authViewModel::onEmailChange,
                        onPasswordChange = authViewModel::onPasswordChange,
                        onToggleMode = authViewModel::toggleMode,
                        onSubmit = authViewModel::submit
                    )
                }

                composable(AppDestination.Home.route) {
                    HomeScreen(
                        uiState = movieState,
                        favouriteIds = favouritesState.favouriteIds,
                        onMovieClick = { movieId ->
                            navController.navigate(AppDestination.Details.createRoute(movieId))
                        },
                        onToggleFavourite = favouritesViewModel::toggleFavourite,
                        onRentMovie = { movie ->
                            rentalViewModel.rentMovie(movie, 1)
                        },
                        onRefresh = { movieViewModel.refreshMovies(isPullToRefresh = true) }
                    )
                }

                composable(AppDestination.Search.route) {
                    SearchScreen(
                        uiState = movieState,
                        favouriteIds = favouritesState.favouriteIds,
                        onQueryChange = movieViewModel::onSearchQueryChange,
                        onMovieClick = { movieId ->
                            navController.navigate(AppDestination.Details.createRoute(movieId))
                        },
                        onToggleFavourite = favouritesViewModel::toggleFavourite,
                        onRentMovie = { movie ->
                            rentalViewModel.rentMovie(movie, 1)
                        }
                    )
                }

                composable(AppDestination.Favourites.route) {
                    FavouritesScreen(
                        uiState = favouritesState,
                        onMovieClick = { movieId ->
                            navController.navigate(AppDestination.Details.createRoute(movieId))
                        },
                        onToggleFavourite = favouritesViewModel::toggleFavourite,
                        onRentMovie = { movie ->
                            rentalViewModel.rentMovie(movie, 1)
                        }
                    )
                }

                composable(AppDestination.Rentals.route) {
                    RentalsScreen(
                        uiState = rentalState,
                        onIncreaseDays = rentalViewModel::increaseDays,
                        onDecreaseDays = rentalViewModel::decreaseDays,
                        onRemoveRental = rentalViewModel::removeRental
                    )
                }

                composable(AppDestination.Profile.route) {
                    ProfileScreen(
                        email = authState.currentUserEmail,
                        favouritesCount = favouritesState.favouriteIds.size,
                        rentalsCount = rentalState.rentals.size,
                        onLogout = authViewModel::logout
                    )
                }

                composable(
                    route = AppDestination.Details.route,
                    arguments = listOf(
                        navArgument("movieId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getString("movieId").orEmpty()
                    val movie = movieViewModel.getMovie(movieId)
                    MovieDetailsScreen(
                        movie = movie,
                        isFavourite = movieId in favouritesState.favouriteIds,
                        onToggleFavourite = { favouritesViewModel.toggleFavourite(movieId) },
                        onRentMovie = { days ->
                            movie?.let { rentalViewModel.rentMovie(it, days) }
                        }
                    )
                }
            }
        }
    }
}
