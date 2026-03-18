package com.mad.movieexplorer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mad.movieexplorer.data.repository.MovieRepository
import com.mad.movieexplorer.domain.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val movies: List<Movie> = emptyList(),
    val groupedMovies: Map<String, List<Movie>> = emptyMap(),
    val searchQuery: String = "",
    val filteredMovies: List<Movie> = emptyList()
)

class MovieViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        observeMovies()
        refreshMovies()
    }

    fun onSearchQueryChange(query: String) {
        this.query.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun refreshMovies(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = state.movies.isEmpty(),
                    isRefreshing = isPullToRefresh,
                    errorMessage = null
                )
            }

            val result = movieRepository.refreshMovies()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun getMovie(movieId: String): Movie? = uiState.value.movies.firstOrNull { it.id == movieId }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun observeMovies() {
        viewModelScope.launch {
            combine(movieRepository.movies, query) { movies, activeQuery ->
                val groupedMovies = movies
                    .flatMap { movie ->
                        movie.genres.ifEmpty { listOf("Unsorted") }.map { genre ->
                            genre to movie
                        }
                    }
                    .groupBy(
                        keySelector = { it.first },
                        valueTransform = { it.second }
                    )
                    .mapValues { (_, values) -> values.distinctBy(Movie::id) }
                    .toSortedMap()

                val filteredMovies = if (activeQuery.isBlank()) {
                    movies
                } else {
                    movies.filter { movie ->
                        movie.title.contains(activeQuery.trim(), ignoreCase = true)
                    }
                }

                Triple(movies, groupedMovies, filteredMovies)
            }.collect { (movies, groupedMovies, filteredMovies) ->
                _uiState.update {
                    it.copy(
                        movies = movies,
                        groupedMovies = groupedMovies,
                        filteredMovies = filteredMovies,
                        isLoading = false
                    )
                }
            }
        }
    }
}
