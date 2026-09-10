package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.R
import com.daily.nexamartpartner.features.auth.domain.model.AuthState
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationGuardTest {
    @Test
    fun `unauthenticated request to admin is redirected to login`() {
        val destination = NavigationGuard.resolveAuthorizedDestination(
            authState = AuthState.Unauthenticated,
            requestedDestinationId = R.id.adminOrdersFragment
        )
        assertEquals(R.id.loginFragment, destination)
    }

    @Test
    fun `authenticated admin request to delivery is redirected to admin graph`() {
        val destination = NavigationGuard.resolveAuthorizedDestination(
            authState = AuthState.AuthenticatedAdmin(testSession(UserRole.ADMIN)),
            requestedDestinationId = R.id.deliveryHistoryFragment
        )
        assertEquals(R.id.adminGraph, destination)
    }

    @Test
    fun `unknown route falls back to auth state destination`() {
        val destination = NavigationGuard.resolveAuthorizedDestination(
            authState = AuthState.AuthenticatedDeliveryPartner(testSession(UserRole.DELIVERY_PARTNER)),
            requestedDestinationId = Int.MAX_VALUE
        )
        assertEquals(R.id.deliveryGraph, destination)
    }

    @Test
    fun `delivery request to admin partner details redirects to delivery graph`() {
        val destination = NavigationGuard.resolveAuthorizedDestination(
            AuthState.AuthenticatedDeliveryPartner(testSession(UserRole.DELIVERY_PARTNER)),
            R.id.adminDeliveryPartnerDetailsFragment
        )
        assertEquals(R.id.deliveryGraph, destination)
    }

    @Test
    fun `delivery request to admin product management redirects to delivery graph`() {
        val destination = NavigationGuard.resolveAuthorizedDestination(
            AuthState.AuthenticatedDeliveryPartner(testSession(UserRole.DELIVERY_PARTNER)),
            R.id.adminProductsFragment
        )
        assertEquals(R.id.deliveryGraph, destination)
    }

    @Test
    fun `authenticated admin request to product form is authorized`() {
        val destination = NavigationGuard.resolveAuthorizedDestination(
            AuthState.AuthenticatedAdmin(testSession(UserRole.ADMIN)),
            R.id.adminProductFormFragment
        )
        assertEquals(R.id.adminProductFormFragment, destination)
    }

    private fun testSession(role: UserRole): UserSession {
        return UserSession(
            accessToken = "access",
            refreshToken = "refresh",
            userId = 12L,
            name = "User",
            contact = "9999999999",
            role = role
        )
    }
}
