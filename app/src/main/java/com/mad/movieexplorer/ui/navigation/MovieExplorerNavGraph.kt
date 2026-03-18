package com.mad.movieexplorer.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalMovies
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
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
import com.mad.movieexplorer.ui.components.GlassIconButton
import com.mad.movieexplorer.ui.components.GlassSurface
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
    val showHomeActions = currentRoute == AppDestination.Home.route

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
                if (showHomeActions) {
                    HomeHeader(
                        onSearchClick = {
                            navController.navigate(AppDestination.Search.route)
                        },
                        onProfileClick = {
                            navController.navigate(AppDestination.Profile.route)
                        }
                    )
                } else {
                    ScreenHeader(
                        title = currentRoute.toScreenTitle(),
                        showBack = currentRoute == AppDestination.Details.route,
                        onBackClick = { navController.popBackStack() }
                    )
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
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            radius = 900f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                                Color.Transparent,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
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
                            rentalsByMovieId = rentalState.rentalsByMovieId,
                            onMovieClick = { movieId ->
                                navController.navigate(AppDestination.Details.createRoute(movieId))
                            },
                            onToggleFavourite = favouritesViewModel::toggleFavourite,
                            onRentMovie = { movie ->
                                rentalViewModel.rentMovie(movie, 1)
                            },
                            onIncreaseRentalDays = rentalViewModel::increaseDaysForMovie,
                            onDecreaseRentalDays = rentalViewModel::decreaseDaysForMovie,
                            onRefresh = { movieViewModel.refreshMovies(isPullToRefresh = true) }
                        )
                    }

                    composable(AppDestination.Search.route) {
                        SearchScreen(
                            uiState = movieState,
                            favouriteIds = favouritesState.favouriteIds,
                            rentalsByMovieId = rentalState.rentalsByMovieId,
                            onQueryChange = movieViewModel::onSearchQueryChange,
                            onMovieClick = { movieId ->
                                navController.navigate(AppDestination.Details.createRoute(movieId))
                            },
                            onToggleFavourite = favouritesViewModel::toggleFavourite,
                            onRentMovie = { movie ->
                                rentalViewModel.rentMovie(movie, 1)
                            },
                            onIncreaseRentalDays = rentalViewModel::increaseDaysForMovie,
                            onDecreaseRentalDays = rentalViewModel::decreaseDaysForMovie
                        )
                    }

                    composable(AppDestination.Favourites.route) {
                        FavouritesScreen(
                            uiState = favouritesState,
                            rentalsByMovieId = rentalState.rentalsByMovieId,
                            onMovieClick = { movieId ->
                                navController.navigate(AppDestination.Details.createRoute(movieId))
                            },
                            onToggleFavourite = favouritesViewModel::toggleFavourite,
                            onRentMovie = { movie ->
                                rentalViewModel.rentMovie(movie, 1)
                            },
                            onIncreaseRentalDays = rentalViewModel::increaseDaysForMovie,
                            onDecreaseRentalDays = rentalViewModel::decreaseDaysForMovie
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
                            activeRental = rentalState.rentalsByMovieId[movieId],
                            isFavourite = movieId in favouritesState.favouriteIds,
                            onToggleFavourite = { favouritesViewModel.toggleFavourite(movieId) },
                            onRentMovie = { days ->
                                movie?.let { rentalViewModel.rentMovie(it, days) }
                            },
                            onIncreaseDays = {
                                movie?.let(rentalViewModel::increaseDaysForMovie)
                            },
                            onDecreaseDays = {
                                rentalViewModel.decreaseDaysForMovie(movieId)
                            }
                        )
                    }
                }

                if (showBottomBar) {
                    Box(
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 18.dp)
                    ) {
                        GlassSurface(
                            shape = RoundedCornerShape(36.dp),
                            containerColor = DeepOcean.copy(alpha = 0.72f),
                            borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                            accentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color.White.copy(alpha = 0.10f),
                                                Color.Transparent
                                            ),
                                            radius = 220f
                                        ),
                                        alpha = 0.35f
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    bottomNavItems.forEach { item ->
                                        val selected = currentRoute == item.destination.route
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .background(
                                                    brush = if (selected) {
                                                        Brush.radialGradient(
                                                            colors = listOf(
                                                                Color(0xFFC49782).copy(alpha = 0.70f),
                                                                Color(0xFF8E6B61).copy(alpha = 0.45f)
                                                            )
                                                        )
                                                    } else {
                                                        Brush.radialGradient(
                                                            colors = listOf(
                                                                Color.Transparent,
                                                                Color.Transparent
                                                            )
                                                        )
                                                    },
                                                    shape = CircleShape
                                                )
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    navController.navigate(item.destination.route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                                modifier = Modifier.size(52.dp)
                                            ) {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.label,
                                                    tint = Color.White.copy(
                                                        alpha = if (selected) 1f else 0.92f
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFB247),
                                Color(0xFFFF7D2D)
                            )
                        ),
                        shape = RoundedCornerShape(
                            topStart = 6.dp,
                            topEnd = 12.dp,
                            bottomEnd = 8.dp,
                            bottomStart = 3.dp
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalMovies,
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp)
                        .align(androidx.compose.ui.Alignment.Center),
                    tint = Color(0xFF20160D)
                )
            }
            Text(
                text = "Movie Explorer",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            HeaderActionButton(
                icon = Icons.Outlined.Search,
                contentDescription = "Search",
                onClick = onSearchClick
            )
            HeaderActionButton(
                icon = Icons.Outlined.Person,
                contentDescription = "Profile",
                onClick = onProfileClick
            )
        }
    }
}

@Composable
private fun ScreenHeader(
    title: String,
    showBack: Boolean,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            if (showBack) {
                HeaderActionButton(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBackClick
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        if (!showBack) {
            Box(modifier = Modifier.size(42.dp))
        }
    }
}

@Composable
private fun HeaderActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f)
        )
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(42.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private fun String?.toScreenTitle(): String {
    return when (this) {
        AppDestination.Search.route -> "Search"
        AppDestination.Favourites.route -> "Favourites"
        AppDestination.Rentals.route -> "Rentals"
        AppDestination.Profile.route -> "Profile"
        AppDestination.Details.route -> "Details"
        else -> "Movie Explorer"
    }
}
