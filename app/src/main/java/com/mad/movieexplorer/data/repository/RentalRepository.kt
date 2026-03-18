package com.mad.movieexplorer.data.repository

import com.mad.movieexplorer.data.local.dao.RentalDao
import com.mad.movieexplorer.data.local.entity.RentalEntity
import com.mad.movieexplorer.data.local.entity.toDomain
import com.mad.movieexplorer.domain.model.Movie
import com.mad.movieexplorer.domain.model.Rental
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RentalRepository(
    private val rentalDao: RentalDao
) {
    val rentals: Flow<List<Rental>> = rentalDao.getAllRentals().map { entities ->
        entities.map(RentalEntity::toDomain)
    }

    suspend fun rentMovie(movie: Movie, days: Int): Result<Unit> {
        if (days <= 0) {
            return Result.failure(IllegalArgumentException("Select at least one rental day."))
        }

        val existingRental = rentalDao.getRentalByMovieId(movie.id)
        if (existingRental == null) {
            rentalDao.insertRental(
                RentalEntity(
                    movieId = movie.id,
                    title = movie.title,
                    posterUrl = movie.posterUrl,
                    rating = movie.rating,
                    pricePerDay = generatePrice(movie.id),
                    days = days
                )
            )
        } else {
            rentalDao.updateRentalDays(existingRental.id, existingRental.days + days)
        }
        return Result.success(Unit)
    }

    suspend fun updateRentalDays(rentalId: Long, days: Int) {
        val rental = rentalDao.getRentalById(rentalId) ?: return
        if (days <= 0) {
            rentalDao.deleteRental(rental)
        } else {
            rentalDao.updateRentalDays(rentalId, days)
        }
    }

    suspend fun deleteRental(rentalId: Long) {
        val rental = rentalDao.getRentalById(rentalId) ?: return
        rentalDao.deleteRental(rental)
    }

    private fun generatePrice(movieId: String): Double {
        val seed = movieId.hashCode().absoluteValue
        return Random(seed).nextInt(from = 50, until = 201).toDouble()
    }
}
