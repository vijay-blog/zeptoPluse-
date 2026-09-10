package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthorizationPolicyTest {
    @Test
    fun `admin can access admin destination`() {
        assertTrue(AuthorizationPolicy.canAccess(UserRole.ADMIN, AppDestination.ADMIN_ORDERS))
    }

    @Test
    fun `admin cannot access delivery destination`() {
        assertFalse(AuthorizationPolicy.canAccess(UserRole.ADMIN, AppDestination.DELIVERY_HISTORY))
    }

    @Test
    fun `delivery can access delivery destination`() {
        assertTrue(
            AuthorizationPolicy.canAccess(
                UserRole.DELIVERY_PARTNER,
                AppDestination.DELIVERY_ASSIGNED_ORDERS
            )
        )
    }

    @Test
    fun `delivery cannot access admin destination`() {
        assertFalse(
            AuthorizationPolicy.canAccess(
                UserRole.DELIVERY_PARTNER,
                AppDestination.ADMIN_PRODUCTS
            )
        )
    }

    @Test
    fun `delivery cannot access admin delivery partner management`() {
        assertFalse(
            AuthorizationPolicy.canAccess(
                UserRole.DELIVERY_PARTNER,
                AppDestination.ADMIN_DELIVERY_PARTNERS
            )
        )
    }

    @Test
    fun `admin can access product details and form destinations`() {
        assertTrue(AuthorizationPolicy.canAccess(UserRole.ADMIN, AppDestination.ADMIN_PRODUCT_DETAILS))
        assertTrue(AuthorizationPolicy.canAccess(UserRole.ADMIN, AppDestination.ADMIN_PRODUCT_FORM))
    }

    @Test
    fun `delivery cannot access product details and form destinations`() {
        assertFalse(AuthorizationPolicy.canAccess(UserRole.DELIVERY_PARTNER, AppDestination.ADMIN_PRODUCT_DETAILS))
        assertFalse(AuthorizationPolicy.canAccess(UserRole.DELIVERY_PARTNER, AppDestination.ADMIN_PRODUCT_FORM))
    }
}
