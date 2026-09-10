package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.features.auth.domain.model.AuthState

object AuthDestinationResolver {
    fun resolve(state: AuthState): Int {
        return when (state) {
            is AuthState.Loading -> R.id.splashFragment
            is AuthState.Unauthenticated,
            is AuthState.AuthenticationError -> R.id.loginFragment
            is AuthState.AuthenticatedAdmin -> R.id.adminGraph
            is AuthState.AuthenticatedDeliveryPartner -> R.id.deliveryGraph
            is AuthState.UnsupportedRole -> R.id.unsupportedRoleFragment
        }
    }
}
