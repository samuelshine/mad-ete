package com.mad.movieexplorer.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.mad.movieexplorer.domain.model.Movie

data class MovieResponseDto(
    @SerializedName("data") val data: List<MovieDto> = emptyList()
)

data class MovieDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("year") val year: String = "",
    @SerializedName("rated") val rated: String = "",
    @SerializedName("released") val released: String = "",
    @SerializedName("runtime") val runtime: String = "",
    @SerializedName("genre") val genre: String = "",
    @SerializedName("director") val director: String = "",
    @SerializedName("writer") val writer: String = "",
    @SerializedName("actors") val actors: String = "",
    @SerializedName("plot") val plot: String = "",
    @SerializedName("language") val language: String = "",
    @SerializedName("country") val country: String = "",
    @SerializedName("awards") val awards: String = "",
    @SerializedName("poster") val poster: String = "",
    @SerializedName("imdbRating") val imdbRating: String = "",
    @SerializedName("imdbId") val imdbId: String = "",
    @SerializedName("boxOffice") val boxOffice: String = ""
)

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id.ifBlank { title },
        title = title.ifBlank { "Untitled Movie" },
        posterUrl = poster,
        rating = imdbRating.toDoubleOrNull() ?: 0.0,
        genres = genre
            .split(",")
            .map(String::trim)
            .filter(String::isNotEmpty),
        overview = plot.ifBlank { "Overview unavailable." },
        year = year,
        rated = rated,
        released = released,
        runtime = runtime,
        director = director,
        writer = writer,
        actors = actors,
        language = language,
        country = country,
        awards = awards,
        imdbId = imdbId,
        boxOffice = boxOffice
    )
}
