package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.features.auth.domain.model.AuthState
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthDestinationResolverTest {
    @Test
    fun `unauthenticated routes to login`() {
        val destination = AuthDestinationResolver.resolve(AuthState.Unauthenticated)
        assertEquals(R.id.loginFragment, destination)
    }

    @Test
    fun `loading routes to splash`() {
        val destination = AuthDestinationResolver.resolve(AuthState.Loading)
        assertEquals(R.id.splashFragment, destination)
    }

    @Test
    fun `admin routes to admin placeholder`() {
        val destination = AuthDestinationResolver.resolve(
            AuthState.AuthenticatedAdmin(testSession(UserRole.ADMIN))
        )
        assertEquals(R.id.adminGraph, destination)
    }

    @Test
    fun `delivery partner routes to delivery placeholder`() {
        val destination = AuthDestinationResolver.resolve(
            AuthState.AuthenticatedDeliveryPartner(testSession(UserRole.DELIVERY_PARTNER))
        )
        assertEquals(R.id.deliveryGraph, destination)
    }

    @Test
    fun `unsupported role routes to denied screen`() {
        val destination = AuthDestinationResolver.resolve(
            AuthState.UnsupportedRole("This account does not have permission to use the NexaMart Admin & Delivery application.")
        )
        assertEquals(R.id.unsupportedRoleFragment, destination)
    }

    private fun testSession(role: UserRole): UserSession {
        return UserSession(
            accessToken = "access",
            refreshToken = "refresh",
            userId = 22L,
            name = "User",
            contact = "9999999999",
            role = role
        )
    }
}
