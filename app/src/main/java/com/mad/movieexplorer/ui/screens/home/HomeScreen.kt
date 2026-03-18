package com.mad.movieexplorer.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mad.movieexplorer.domain.model.Movie
import com.mad.movieexplorer.ui.components.InlineErrorCard
import com.mad.movieexplorer.ui.components.LoadingView
import com.mad.movieexplorer.ui.components.MoviePosterCard
import com.mad.movieexplorer.ui.components.formatRating
import com.mad.movieexplorer.ui.theme.Night
import com.mad.movieexplorer.viewmodel.MovieUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: MovieUiState,
    favouriteIds: Set<String>,
    onMovieClick: (String) -> Unit,
    onToggleFavourite: (String) -> Unit,
    onRentMovie: (Movie) -> Unit,
    onRefresh: () -> Unit
) {
    if (uiState.isLoading && uiState.movies.isEmpty()) {
        LoadingView()
        return
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { onRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Tonight's lineup",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Discover standout films grouped by genre, save favourites, and rent in a tap.",
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            uiState.movies.maxByOrNull(Movie::rating)?.let { featuredMovie ->
                item {
                    FeaturedMovieBanner(
                        movie = featuredMovie,
                        onClick = { onMovieClick(featuredMovie.id) },
                        onRentMovie = { onRentMovie(featuredMovie) }
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = !uiState.errorMessage.isNullOrBlank(),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    InlineErrorCard(message = uiState.errorMessage.orEmpty())
                }
            }

            items(uiState.groupedMovies.toList(), key = { it.first }) { (genre, movies) ->
                Column {
                    Text(
                        text = genre,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LazyRow(
                        modifier = Modifier.padding(top = 14.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(movies, key = { it.id }) { movie ->
                            MoviePosterCard(
                                movie = movie,
                                isFavourite = movie.id in favouriteIds,
                                onToggleFavourite = { onToggleFavourite(movie.id) },
                                onRentClick = { onRentMovie(movie) },
                                onClick = { onMovieClick(movie.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedMovieBanner(
    movie: Movie,
    onClick: () -> Unit,
    onRentMovie: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Night.copy(alpha = 0.1f),
                                Night.copy(alpha = 0.45f),
                                Night.copy(alpha = 0.96f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Featured pick",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = movie.title,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatRating(movie.rating)} IMDb  •  ${movie.runtime.ifBlank { "Runtime unavailable" }}",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = movie.overview,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(260.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = onRentMovie) {
                        Text(text = "Rent instantly")
                    }
                }
            }
        }
    }
}
