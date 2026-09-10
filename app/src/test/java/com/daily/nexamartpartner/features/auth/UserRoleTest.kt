package com.daily.nexamartpartner.features.auth

import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Test

class UserRoleTest {
    @Test
    fun `parses admin role`() {
        assertEquals(UserRole.ADMIN, UserRole.fromRaw("ADMIN"))
    }

    @Test
    fun `parses delivery partner role`() {
        assertEquals(UserRole.DELIVERY_PARTNER, UserRole.fromRaw("DELIVERY_PARTNER"))
    }

    @Test
    fun `maps unsupported role`() {
        assertEquals(UserRole.UNSUPPORTED, UserRole.fromRaw("CUSTOMER"))
    }
}
