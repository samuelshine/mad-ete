package com.mad.movieexplorer.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mad.movieexplorer.data.local.dao.RentalDao
import com.mad.movieexplorer.data.local.entity.RentalEntity

@Database(
    entities = [RentalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieExplorerDatabase : RoomDatabase() {
    abstract fun rentalDao(): RentalDao

    companion object {
        @Volatile
        private var INSTANCE: MovieExplorerDatabase? = null

        fun getInstance(context: Context): MovieExplorerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MovieExplorerDatabase::class.java,
                    "movie_explorer.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
