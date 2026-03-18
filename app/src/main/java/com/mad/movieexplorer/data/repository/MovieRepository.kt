package com.mad.movieexplorer.data.repository

import com.mad.movieexplorer.data.remote.api.MovieApiService
import com.mad.movieexplorer.data.remote.dto.toDomain
import com.mad.movieexplorer.domain.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MovieRepository(
    private val apiService: MovieApiService
) {
    private val cachedMovies = MutableStateFlow<List<Movie>>(emptyList())

    val movies: StateFlow<List<Movie>> = cachedMovies.asStateFlow()

    suspend fun refreshMovies(): Result<List<Movie>> {
        return try {
            val remoteMovies = apiService.getMovies().data.map { dto -> dto.toDomain() }
            val finalMovies = remoteMovies.ifEmpty { sampleMovies }
            cachedMovies.value = finalMovies
            Result.success(finalMovies)
        } catch (exception: Exception) {
            val fallbackMovies = cachedMovies.value.ifEmpty { sampleMovies }
            cachedMovies.value = fallbackMovies
            Result.failure(
                IllegalStateException(
                    "We couldn't refresh the catalog, so a fallback collection is being shown.",
                    exception
                )
            )
        }
    }

    fun getMovieById(id: String): Movie? = movies.value.firstOrNull { it.id == id }
}
