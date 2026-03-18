package com.mad.movieexplorer.ui.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mad.movieexplorer.domain.model.Movie
import com.mad.movieexplorer.domain.model.Rental
import com.mad.movieexplorer.ui.components.EmptyStateView
import com.mad.movieexplorer.ui.components.GlassSurface
import com.mad.movieexplorer.ui.components.RentalDayControl
import com.mad.movieexplorer.ui.components.formatCurrency
import com.mad.movieexplorer.ui.components.formatRating
import com.mad.movieexplorer.ui.theme.DeepOcean
import com.mad.movieexplorer.ui.theme.Night

@Composable
fun MovieDetailsScreen(
    movie: Movie?,
    activeRental: Rental?,
    isFavourite: Boolean,
    onBackClick: () -> Unit,
    onToggleFavourite: () -> Unit,
    onRentMovie: (Int) -> Unit,
    onIncreaseDays: () -> Unit,
    onDecreaseDays: () -> Unit
) {
    if (movie == null) {
        EmptyStateView(
            title = "Movie unavailable",
            description = "We couldn't load this title right now."
        )
        return
    }

    var rentalDays by rememberSaveable { mutableIntStateOf(3) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(movie.id) {
        contentVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Night, Color(0xFF04100E), DeepOcean)
                )
            )
    ) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(animationSpec = tween(450)) +
                slideInVertically(
                    animationSpec = tween(450),
                    initialOffsetY = { it / 10 }
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    HeroSection(
                        movie = movie,
                        isFavourite = isFavourite,
                        onBackClick = onBackClick,
                        onToggleFavourite = onToggleFavourite
                    )
                }

                item {
                    DetailBody(
                        movie = movie,
                        activeRental = activeRental,
                        rentalDays = rentalDays,
                        onRentalDaysChange = { rentalDays = it },
                        onRentMovie = { onRentMovie(rentalDays) },
                        onIncreaseDays = onIncreaseDays,
                        onDecreaseDays = onDecreaseDays
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSection(
    movie: Movie,
    isFavourite: Boolean,
    onBackClick: () -> Unit,
    onToggleFavourite: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        bottomStart = 36.dp,
                        bottomEnd = 36.dp
                    )
                ),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Night.copy(alpha = 0.24f),
                            Color.Transparent,
                            Night.copy(alpha = 0.16f),
                            Night.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeroCircleButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                onClick = onBackClick
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HeroCircleButton(
                    icon = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favourite",
                    onClick = onToggleFavourite
                )
                HeroCircleButton(
                    icon = Icons.Outlined.MoreHoriz,
                    contentDescription = "More",
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun DetailBody(
    movie: Movie,
    activeRental: Rental?,
    rentalDays: Int,
    onRentalDaysChange: (Int) -> Unit,
    onRentMovie: () -> Unit,
    onIncreaseDays: () -> Unit,
    onDecreaseDays: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 20.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = listOf(movie.year, movie.runtime, movie.rated, movie.language.takeIf { it.isNotBlank() })
                .filterNotNull()
                .filter(String::isNotBlank)
                .joinToString("  •  "),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = movie.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE2C23F)
                ) {
                    Text(
                        text = "IMDb",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = Color(0xFF222222),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    text = " ${formatRating(movie.rating)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Text(
            text = movie.overview,
            modifier = Modifier.padding(top = 14.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyRow(
            modifier = Modifier.padding(top = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(buildDetailHighlights(movie), key = { it.title }) { item ->
                DetailHighlightCard(
                    title = item.title,
                    value = item.value
                )
            }
        }

        GlassSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White.copy(alpha = 0.06f),
            borderColor = Color.White.copy(alpha = 0.08f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (activeRental == null) "Choose rental days" else "Manage rental",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (activeRental != null) {
                    Text(
                        text = "${activeRental.days} day${if (activeRental.days == 1) "" else "s"} active",
                        modifier = Modifier.padding(top = 14.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Active total: ${formatCurrency(activeRental.totalPrice)}",
                        modifier = Modifier.padding(top = 14.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.padding(top = 14.dp)) {
                        RentalDayControl(
                            activeRental = activeRental,
                            onRentClick = onRentMovie,
                            onIncreaseDays = onIncreaseDays,
                            onDecreaseDays = onDecreaseDays
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$rentalDays day${if (rentalDays == 1) "" else "s"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            HeroCircleButton(
                                icon = Icons.Filled.Remove,
                                contentDescription = "Decrease selected rental days",
                                onClick = { onRentalDaysChange((rentalDays - 1).coerceAtLeast(1)) },
                                compact = true
                            )
                            HeroCircleButton(
                                icon = Icons.Outlined.Add,
                                contentDescription = "Increase selected rental days",
                                onClick = { onRentalDaysChange(rentalDays + 1) },
                                compact = true
                            )
                        }
                    }
                    Button(
                        onClick = onRentMovie,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "Rent movie")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    compact: Boolean = false
) {
    Surface(
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.10f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.12f)
        )
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(if (compact) 38.dp else 46.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun DetailHighlightCard(
    title: String,
    value: String
) {
    GlassSurface(
        modifier = Modifier
            .width(196.dp)
            .height(132.dp),
        shape = RoundedCornerShape(26.dp),
        containerColor = Color.White.copy(alpha = 0.05f),
        borderColor = Color.White.copy(alpha = 0.08f),
        highlightColor = Color.White.copy(alpha = 0.05f),
        accentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class DetailHighlight(
    val title: String,
    val value: String
)

private fun buildDetailHighlights(movie: Movie): List<DetailHighlight> {
    return listOfNotNull(
        movie.director.takeIf { it.isNotBlank() }?.let {
            DetailHighlight("Director", it)
        },
        movie.actors.takeIf { it.isNotBlank() }?.let {
            DetailHighlight("Cast", it)
        },
        movie.awards.takeIf { it.isNotBlank() }?.let {
            DetailHighlight("Awards", it)
        },
        movie.language.takeIf { it.isNotBlank() }?.let {
            DetailHighlight("Language", it)
        },
        movie.boxOffice.takeIf { it.isNotBlank() }?.let {
            DetailHighlight("Box Office", it)
        },
        movie.country.takeIf { it.isNotBlank() }?.let {
            DetailHighlight("Country", it)
        }
    ).ifEmpty {
        listOf(
            DetailHighlight("Mood", "Cinematic and immersive"),
            DetailHighlight("Watch for", "Strong atmosphere and standout performances"),
            DetailHighlight("Format", movie.genres.joinToString().ifBlank { "Feature film" })
        )
    }
}
