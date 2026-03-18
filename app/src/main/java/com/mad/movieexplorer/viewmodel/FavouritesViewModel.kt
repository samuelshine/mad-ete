package com.mad.movieexplorer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mad.movieexplorer.data.repository.FavouritesRepository
import com.mad.movieexplorer.data.repository.MovieRepository
import com.mad.movieexplorer.domain.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavouritesUiState(
    val favouriteIds: Set<String> = emptySet(),
    val favouriteMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = true,
    val message: String? = null
)

class FavouritesViewModel(
    private val favouritesRepository: FavouritesRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavouritesUiState())
    val uiState: StateFlow<FavouritesUiState> = _uiState.asStateFlow()

    init {
        observeFavourites()
        if (movieRepository.movies.value.isEmpty()) {
            viewModelScope.launch {
                movieRepository.refreshMovies()
            }
        }
    }

    fun toggleFavourite(movieId: String) {
        viewModelScope.launch {
            favouritesRepository.toggleFavourite(movieId)
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            combine(
                favouritesRepository.favouriteIds,
                movieRepository.movies
            ) { favouriteIds, movies ->
                favouriteIds to movies.filter { it.id in favouriteIds }
            }.collect { (favouriteIds, favouriteMovies) ->
                _uiState.update {
                    it.copy(
                        favouriteIds = favouriteIds,
                        favouriteMovies = favouriteMovies,
                        isLoading = false
                    )
                }
            }
        }
    }
}
