package com.mad.movieexplorer.ui.screens.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mad.movieexplorer.domain.model.Movie
import com.mad.movieexplorer.domain.model.Rental
import com.mad.movieexplorer.ui.components.EmptyStateView
import com.mad.movieexplorer.ui.components.GlassSurface
import com.mad.movieexplorer.ui.components.LoadingView
import com.mad.movieexplorer.ui.components.RentalDayControl
import com.mad.movieexplorer.ui.components.formatRating
import com.mad.movieexplorer.ui.theme.DeepOcean
import com.mad.movieexplorer.ui.theme.Night
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

    val genreFilters = remember(uiState.favouriteMovies) {
        buildList {
            add("All")
            addAll(
                uiState.favouriteMovies
                    .flatMap { it.genres }
                    .distinct()
                    .take(6)
            )
        }
    }

    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    val filteredMovies = remember(uiState.favouriteMovies, selectedFilter) {
        if (selectedFilter == "All") {
            uiState.favouriteMovies
        } else {
            uiState.favouriteMovies.filter { movie -> selectedFilter in movie.genres }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { filteredMovies.size.coerceAtLeast(1) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 120.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(top = 16.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(genreFilters, key = { it }) { filter ->
                FavouriteFilterChip(
                    label = filter,
                    selected = filter == selectedFilter,
                    onClick = { selectedFilter = filter }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            contentPadding = PaddingValues(horizontal = 28.dp),
            pageSpacing = 16.dp
        ) { page ->
            val movie = filteredMovies.getOrNull(page) ?: return@HorizontalPager
            FavouriteCarouselCard(
                movie = movie,
                activeRental = rentalsByMovieId[movie.id],
                onClick = { onMovieClick(movie.id) },
                onToggleFavourite = { onToggleFavourite(movie.id) },
                onRentMovie = { onRentMovie(movie) },
                onIncreaseRentalDays = { onIncreaseRentalDays(movie) },
                onDecreaseRentalDays = { onDecreaseRentalDays(movie.id) }
            )
        }
    }
}

@Composable
private fun FavouriteFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.32f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.16f)
        },
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FavouriteCarouselCard(
    movie: Movie,
    activeRental: Rental?,
    onClick: () -> Unit,
    onToggleFavourite: () -> Unit,
    onRentMovie: () -> Unit,
    onIncreaseRentalDays: () -> Unit,
    onDecreaseRentalDays: () -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(640.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(34.dp),
        containerColor = DeepOcean.copy(alpha = 0.22f),
        borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(34.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Night.copy(alpha = 0.08f),
                                Night.copy(alpha = 0.18f),
                                Night.copy(alpha = 0.82f),
                                Night.copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .padding(18.dp)
                    .align(Alignment.TopEnd)
                    .size(46.dp)
                    .background(
                        color = Night.copy(alpha = 0.24f),
                        shape = CircleShape
                    )
                    .clickable(onClick = onToggleFavourite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Remove favourite",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.year.ifBlank { "N/A" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = movie.runtime.ifBlank { "Runtime unavailable" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = movie.genres.firstOrNull().orEmpty().ifBlank { "Movie" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatRating(movie.rating),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RentalDayControl(
                        activeRental = activeRental,
                        onRentClick = onRentMovie,
                        onIncreaseDays = onIncreaseRentalDays,
                        onDecreaseDays = onDecreaseRentalDays,
                        compact = true
                    )
                }
            }
        }
    }
}
