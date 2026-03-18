package com.mad.movieexplorer.data.repository

import com.mad.movieexplorer.data.local.datastore.FavouritesPreferences
import kotlinx.coroutines.flow.Flow

class FavouritesRepository(
    private val favouritesPreferences: FavouritesPreferences
) {
    val favouriteIds: Flow<Set<String>> = favouritesPreferences.favouriteIds

    suspend fun toggleFavourite(movieId: String): Set<String> {
        return favouritesPreferences.toggle(movieId)
    }

    suspend fun clearFavourites() {
        favouritesPreferences.clearAll()
    }
}
