package com.mad.movieexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mad.movieexplorer.data.local.entity.RentalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRental(rental: RentalEntity): Long

    @Query("SELECT * FROM rentals ORDER BY title ASC")
    fun getAllRentals(): Flow<List<RentalEntity>>

    @Query("UPDATE rentals SET days = :days WHERE id = :id")
    suspend fun updateRentalDays(id: Long, days: Int)

    @Delete
    suspend fun deleteRental(rental: RentalEntity)

    @Query("SELECT * FROM rentals WHERE movieId = :movieId LIMIT 1")
    suspend fun getRentalByMovieId(movieId: String): RentalEntity?

    @Query("SELECT * FROM rentals WHERE id = :id LIMIT 1")
    suspend fun getRentalById(id: Long): RentalEntity?
}
