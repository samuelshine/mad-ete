package com.mad.movieexplorer

import android.content.Context
import com.mad.movieexplorer.data.local.datastore.AuthPreferences
import com.mad.movieexplorer.data.local.datastore.FavouritesPreferences
import com.mad.movieexplorer.data.local.db.MovieExplorerDatabase
import com.mad.movieexplorer.data.remote.api.NetworkModule
import com.mad.movieexplorer.data.repository.AuthRepository
import com.mad.movieexplorer.data.repository.FavouritesRepository
import com.mad.movieexplorer.data.repository.MovieRepository
import com.mad.movieexplorer.data.repository.RentalRepository

class AppContainer(context: Context) {
    private val database = MovieExplorerDatabase.getInstance(context)

    val authRepository: AuthRepository by lazy {
        AuthRepository(AuthPreferences(context))
    }

    val movieRepository: MovieRepository by lazy {
        MovieRepository(NetworkModule.movieApiService)
    }

    val favouritesRepository: FavouritesRepository by lazy {
        FavouritesRepository(FavouritesPreferences(context))
    }

    val rentalRepository: RentalRepository by lazy {
        RentalRepository(database.rentalDao())
    }
}
