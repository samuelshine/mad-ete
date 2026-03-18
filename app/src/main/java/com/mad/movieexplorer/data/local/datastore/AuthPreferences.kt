package com.mad.movieexplorer.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.mad.movieexplorer.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class StoredAuthData(
    val email: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false
)

class AuthPreferences(context: Context) {
    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("auth_preferences") }
    )

    val authState: Flow<StoredAuthData> = dataStore.data.map { preferences ->
        StoredAuthData(
            email = preferences[EMAIL_KEY].orEmpty(),
            password = preferences[PASSWORD_KEY].orEmpty(),
            isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
        )
    }

    val session: Flow<AuthSession> = authState.map {
        AuthSession(email = it.email, isLoggedIn = it.isLoggedIn)
    }

    suspend fun getStoredAuthData(): StoredAuthData = authState.first()

    suspend fun saveCredentials(
        email: String,
        password: String,
        isLoggedIn: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[PASSWORD_KEY] = password
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    suspend fun updateLoginState(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    private companion object {
        val EMAIL_KEY: Preferences.Key<String> = stringPreferencesKey("email")
        val PASSWORD_KEY: Preferences.Key<String> = stringPreferencesKey("password")
        val IS_LOGGED_IN_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("is_logged_in")
    }
}
