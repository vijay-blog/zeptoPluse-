package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.features.auth.domain.model.UserRole

object AuthorizationPolicy {
    fun canAccess(role: UserRole?, destination: AppDestination): Boolean {
        return when (destination.scope) {
            DestinationScope.PUBLIC -> true
            DestinationScope.ADMIN -> role == UserRole.ADMIN
            DestinationScope.DELIVERY -> role == UserRole.DELIVERY_PARTNER
        }
    }
}
