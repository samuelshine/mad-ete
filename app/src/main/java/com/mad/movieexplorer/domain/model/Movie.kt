package com.mad.movieexplorer.domain.model

data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String,
    val rating: Double,
    val genres: List<String>,
    val overview: String,
    val year: String = "",
    val rated: String = "",
    val released: String = "",
    val runtime: String = "",
    val director: String = "",
    val writer: String = "",
    val actors: String = "",
    val language: String = "",
    val country: String = "",
    val awards: String = "",
    val imdbId: String = "",
    val boxOffice: String = ""
)
