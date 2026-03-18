package com.mad.movieexplorer.data.repository

import com.mad.movieexplorer.data.local.datastore.AuthPreferences
import com.mad.movieexplorer.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val authPreferences: AuthPreferences
) {
    val session: Flow<AuthSession> = authPreferences.session

    suspend fun login(email: String, password: String): Result<Unit> {
        val sanitizedEmail = email.trim()
        val validationError = validateCredentials(sanitizedEmail, password)
        if (validationError != null) {
            return Result.failure(IllegalArgumentException(validationError))
        }

        val storedAuth = authPreferences.getStoredAuthData()
        if (storedAuth.email.isBlank() || storedAuth.password.isBlank()) {
            return Result.failure(IllegalStateException("Create an account before logging in."))
        }

        if (storedAuth.email != sanitizedEmail || storedAuth.password != password) {
            return Result.failure(IllegalArgumentException("Email or password does not match."))
        }

        authPreferences.updateLoginState(isLoggedIn = true)
        return Result.success(Unit)
    }

    suspend fun signup(email: String, password: String): Result<Unit> {
        val sanitizedEmail = email.trim()
        val validationError = validateCredentials(sanitizedEmail, password)
        if (validationError != null) {
            return Result.failure(IllegalArgumentException(validationError))
        }

        authPreferences.saveCredentials(
            email = sanitizedEmail,
            password = password,
            isLoggedIn = true
        )
        return Result.success(Unit)
    }

    suspend fun logout() {
        authPreferences.updateLoginState(isLoggedIn = false)
    }

    private fun validateCredentials(email: String, password: String): String? {
        return when {
            email.isBlank() -> "Email is required."
            !email.contains("@") -> "Enter a valid email address."
            password.length < 6 -> "Password must be at least 6 characters."
            else -> null
        }
    }
}
