package com.mad.movieexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mad.movieexplorer.domain.model.Rental

@Entity(tableName = "rentals")
data class RentalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val movieId: String,
    val title: String,
    val posterUrl: String,
    val rating: Double,
    val pricePerDay: Double,
    val days: Int
)

fun RentalEntity.toDomain(): Rental {
    return Rental(
        id = id,
        movieId = movieId,
        title = title,
        posterUrl = posterUrl,
        rating = rating,
        pricePerDay = pricePerDay,
        days = days
    )
}
