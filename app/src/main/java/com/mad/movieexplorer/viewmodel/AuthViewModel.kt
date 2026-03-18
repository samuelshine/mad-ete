package com.mad.movieexplorer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mad.movieexplorer.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoginMode: Boolean = true,
    val isAuthenticated: Boolean = false,
    val currentUserEmail: String = "",
    val isLoadingSession: Boolean = true,
    val isSubmitting: Boolean = false,
    val message: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeSession()
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                message = null
            )
        }
    }

    fun submit() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isSubmitting = true, message = null) }

            val result = if (currentState.isLoginMode) {
                authRepository.login(currentState.email, currentState.password)
            } else {
                authRepository.signup(currentState.email, currentState.password)
            }

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            password = "",
                            message = if (currentState.isLoginMode) {
                                "Welcome back."
                            } else {
                                "Account created successfully."
                            }
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            message = throwable.message ?: "Authentication failed."
                        )
                    }
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update {
                it.copy(
                    password = "",
                    message = "You have been logged out."
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun observeSession() {
        viewModelScope.launch {
            authRepository.session.collect { session ->
                _uiState.update {
                    it.copy(
                        currentUserEmail = session.email,
                        isAuthenticated = session.isLoggedIn,
                        isLoadingSession = false
                    )
                }
            }
        }
    }
}
