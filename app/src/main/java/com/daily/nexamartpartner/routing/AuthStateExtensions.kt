package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.features.auth.domain.model.AuthState
import com.daily.nexamartpartner.features.auth.domain.model.UserRole

fun AuthState.currentRoleOrNull(): UserRole? {
    return when (this) {
        is AuthState.AuthenticatedAdmin -> UserRole.ADMIN
        is AuthState.AuthenticatedDeliveryPartner -> UserRole.DELIVERY_PARTNER
        else -> null
    }
}
