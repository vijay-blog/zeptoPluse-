package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.features.auth.domain.model.AuthState

object NavigationGuard {
    fun resolveAuthorizedDestination(
        authState: AuthState,
        requestedDestinationId: Int
    ): Int {
        val requestedDestination = AppDestination.fromNavId(requestedDestinationId)
            ?: return AuthDestinationResolver.resolve(authState)

        val role = authState.currentRoleOrNull()
        return if (AuthorizationPolicy.canAccess(role, requestedDestination)) {
            requestedDestinationId
        } else {
            AuthDestinationResolver.resolve(authState)
        }
    }
}
