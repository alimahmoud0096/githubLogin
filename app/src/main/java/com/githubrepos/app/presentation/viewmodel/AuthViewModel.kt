package com.githubrepos.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githubrepos.app.domain.usecase.AuthenticateUseCase
import com.githubrepos.app.domain.usecase.IsAuthenticatedUseCase
import com.githubrepos.app.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authenticateUseCase: AuthenticateUseCase,
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    fun checkAuthenticationStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val isAuthenticated = isAuthenticatedUseCase()
            _uiState.value = _uiState.value.copy(
                isAuthenticated = isAuthenticated,
                isLoading = false
            )
        }
    }

    fun authenticate(code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authenticateUseCase(code).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = false,
                        isLoading = false,
                        error = exception.message ?: "Authentication failed"
                    )
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _uiState.value = _uiState.value.copy(
                isAuthenticated = false,
                error = null
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

