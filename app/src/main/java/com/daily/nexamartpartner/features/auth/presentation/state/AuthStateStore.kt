package com.daily.nexamartpartner.features.auth.presentation.state

import com.daily.nexamartpartner.features.auth.domain.model.AuthState
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthStateStore {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun setLoading() {
        _authState.value = AuthState.Loading
    }

    fun setUnauthenticated() {
        _authState.value = AuthState.Unauthenticated
    }

    fun setAuthenticationError(message: String) {
        _authState.value = AuthState.AuthenticationError(message)
    }

    fun setUnsupportedRole(message: String) {
        _authState.value = AuthState.UnsupportedRole(message)
    }

    fun setAuthenticated(session: UserSession) {
        _authState.value = when (session.role) {
            UserRole.ADMIN -> AuthState.AuthenticatedAdmin(session)
            UserRole.DELIVERY_PARTNER -> AuthState.AuthenticatedDeliveryPartner(session)
            UserRole.UNSUPPORTED -> AuthState.UnsupportedRole("This application is only for Admin and Delivery Partners.")
        }
    }
}
