package com.daily.nexamartpartner.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.domain.model.AuthState
import com.daily.nexamartpartner.features.auth.presentation.state.AuthStateStore
import com.daily.nexamartpartner.features.auth.domain.usecase.LogoutUseCase
import com.daily.nexamartpartner.features.auth.domain.usecase.RestoreSessionUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthCoordinatorViewModel(
    private val restoreSessionUseCase: RestoreSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authStateStore: AuthStateStore
) : ViewModel() {
    val authState: StateFlow<AuthState> = authStateStore.authState

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        authStateStore.setLoading()
        viewModelScope.launch {
            when (val result = restoreSessionUseCase()) {
                is AppResult.Success -> {
                    val session = result.data
                    if (session == null) {
                        authStateStore.setUnauthenticated()
                    } else {
                        authStateStore.setAuthenticated(session)
                    }
                }

                is AppResult.Failure -> authStateStore.setAuthenticationError(result.error.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            authStateStore.setUnauthenticated()
        }
    }

    fun returnToLoginFromUnsupportedRole() {
        viewModelScope.launch {
            logoutUseCase()
            authStateStore.setUnauthenticated()
        }
    }

    fun onSessionExpired() {
        viewModelScope.launch {
            logoutUseCase()
            authStateStore.setUnauthenticated()
        }
    }
}
