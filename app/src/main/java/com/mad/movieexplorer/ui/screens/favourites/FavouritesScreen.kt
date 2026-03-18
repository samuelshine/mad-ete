package com.mad.movieexplorer.ui.screens.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mad.movieexplorer.domain.model.Movie
import com.mad.movieexplorer.domain.model.Rental
import com.mad.movieexplorer.ui.components.EmptyStateView
import com.mad.movieexplorer.ui.components.LoadingView
import com.mad.movieexplorer.ui.components.MovieRowCard
import com.mad.movieexplorer.viewmodel.FavouritesUiState

@Composable
fun FavouritesScreen(
    uiState: FavouritesUiState,
    rentalsByMovieId: Map<String, Rental>,
    onMovieClick: (String) -> Unit,
    onToggleFavourite: (String) -> Unit,
    onRentMovie: (Movie) -> Unit,
    onIncreaseRentalDays: (Movie) -> Unit,
    onDecreaseRentalDays: (String) -> Unit
) {
    if (uiState.isLoading && uiState.favouriteMovies.isEmpty()) {
        LoadingView(message = "Loading favourites...")
        return
    }

    if (uiState.favouriteMovies.isEmpty()) {
        EmptyStateView(
            title = "No favourites yet",
            description = "Tap the heart icon on any movie to build your watchlist."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Favourites",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Your saved collection is ready whenever you want to revisit it.",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(uiState.favouriteMovies, key = { it.id }) { movie ->
            MovieRowCard(
                movie = movie,
                isFavourite = true,
                activeRental = rentalsByMovieId[movie.id],
                onToggleFavourite = { onToggleFavourite(movie.id) },
                onRentClick = { onRentMovie(movie) },
                onIncreaseDays = { onIncreaseRentalDays(movie) },
                onDecreaseDays = { onDecreaseRentalDays(movie.id) },
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}
