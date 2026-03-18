package com.mad.movieexplorer.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouritesPreferences(context: Context) {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("favourites_preferences") }
    )

    val favouriteIds: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[FAVOURITES_KEY] ?: emptySet()
    }

    suspend fun toggle(movieId: String): Set<String> {
        var updatedIds = emptySet<String>()
        dataStore.edit { preferences ->
            val currentIds = preferences[FAVOURITES_KEY] ?: emptySet()
            updatedIds = if (movieId in currentIds) {
                currentIds - movieId
            } else {
                currentIds + movieId
            }
            preferences[FAVOURITES_KEY] = updatedIds
        }
        return updatedIds
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences[FAVOURITES_KEY] = emptySet()
        }
    }

    private companion object {
        val FAVOURITES_KEY: Preferences.Key<Set<String>> = stringSetPreferencesKey("favourite_ids")
    }
}
