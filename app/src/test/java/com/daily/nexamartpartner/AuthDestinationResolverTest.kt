package com.daily.nexamartpartner

import com.daily.nexamartpartner.features.auth.domain.model.AuthState
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import com.daily.nexamartpartner.routing.AuthDestinationResolver
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthDestinationResolverTest {

    @Test
    fun authenticatedAdmin_resolvesToAdminGraphResourceId() {
        assertEquals(
            R.id.adminGraph,
            AuthDestinationResolver.resolve(AuthState.AuthenticatedAdmin(testSession(UserRole.ADMIN)))
        )
    }

    @Test
    fun authenticatedDeliveryPartner_resolvesToDeliveryGraphResourceId() {
        assertEquals(
            R.id.deliveryGraph,
            AuthDestinationResolver.resolve(
                AuthState.AuthenticatedDeliveryPartner(testSession(UserRole.DELIVERY_PARTNER))
            )
        )
    }

    @Test
    fun unauthenticated_resolvesToLoginResourceId() {
        assertEquals(
            R.id.loginFragment,
            AuthDestinationResolver.resolve(AuthState.Unauthenticated)
        )
    }

    @Test
    fun loading_resolvesToSplashResourceId() {
        assertEquals(
            R.id.splashFragment,
            AuthDestinationResolver.resolve(AuthState.Loading)
        )
    }

    private fun testSession(role: UserRole) = UserSession(
        accessToken = "access",
        refreshToken = "refresh",
        userId = 1L,
        name = "User",
        contact = "9999999999",
        role = role
    )
}
