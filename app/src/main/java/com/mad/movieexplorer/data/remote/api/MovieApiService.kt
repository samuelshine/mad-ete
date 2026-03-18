package com.mad.movieexplorer.data.remote.api

import com.mad.movieexplorer.data.remote.dto.MovieResponseDto
import retrofit2.http.GET

interface MovieApiService {
    @GET("api/movies")
    suspend fun getMovies(): MovieResponseDto
}
