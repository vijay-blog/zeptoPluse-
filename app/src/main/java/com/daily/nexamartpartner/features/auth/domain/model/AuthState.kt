package com.daily.nexamartpartner.features.auth.domain.model

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class AuthenticatedAdmin(val session: UserSession) : AuthState()
    data class AuthenticatedDeliveryPartner(val session: UserSession) : AuthState()
    data class UnsupportedRole(val message: String) : AuthState()
    data class AuthenticationError(val message: String) : AuthState()
}
