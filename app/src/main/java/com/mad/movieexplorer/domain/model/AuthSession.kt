package com.mad.movieexplorer.domain.model

data class AuthSession(
    val email: String = "",
    val isLoggedIn: Boolean = false
)
