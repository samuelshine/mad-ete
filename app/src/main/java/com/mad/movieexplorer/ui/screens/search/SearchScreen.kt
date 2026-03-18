package com.mad.movieexplorer.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mad.movieexplorer.domain.model.Movie
import com.mad.movieexplorer.domain.model.Rental
import com.mad.movieexplorer.ui.components.EmptyStateView
import com.mad.movieexplorer.ui.components.InlineErrorCard
import com.mad.movieexplorer.ui.components.LoadingView
import com.mad.movieexplorer.ui.components.MovieRowCard
import com.mad.movieexplorer.viewmodel.MovieUiState
import androidx.compose.material3.Icon

@Composable
fun SearchScreen(
    uiState: MovieUiState,
    favouriteIds: Set<String>,
    rentalsByMovieId: Map<String, Rental>,
    onQueryChange: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onToggleFavourite: (String) -> Unit,
    onRentMovie: (Movie) -> Unit,
    onIncreaseRentalDays: (Movie) -> Unit,
    onDecreaseRentalDays: (String) -> Unit
) {
    if (uiState.isLoading && uiState.movies.isEmpty()) {
        LoadingView()
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Search titles",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Filter the catalog in real time by movie title.",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search by title") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.22f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            )
        }

        if (!uiState.errorMessage.isNullOrBlank()) {
            item {
                InlineErrorCard(message = uiState.errorMessage.orEmpty())
            }
        }

        if (uiState.filteredMovies.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No matches found",
                    description = "Try a different title or browse genres from the home screen.",
                    paddingValues = PaddingValues(vertical = 32.dp, horizontal = 0.dp)
                )
            }
        } else {
            items(uiState.filteredMovies, key = { it.id }) { movie ->
                MovieRowCard(
                    movie = movie,
                    isFavourite = movie.id in favouriteIds,
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
}
