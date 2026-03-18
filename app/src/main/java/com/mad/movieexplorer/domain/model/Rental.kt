package com.mad.movieexplorer.domain.model

data class Rental(
    val id: Long,
    val movieId: String,
    val title: String,
    val posterUrl: String,
    val rating: Double,
    val pricePerDay: Double,
    val days: Int
) {
    val totalPrice: Double
        get() = pricePerDay * days
}
