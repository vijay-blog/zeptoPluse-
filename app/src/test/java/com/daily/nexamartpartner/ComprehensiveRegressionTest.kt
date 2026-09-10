package com.daily.nexamartpartner

import com.daily.nexamartpartner.core.format.ValueFormatter
import com.daily.nexamartpartner.routing.AppDestination
import com.daily.nexamartpartner.routing.AuthorizationPolicy
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import java.math.BigDecimal
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ComprehensiveRegressionTest {
    @Test
    fun `every protected destination has a valid navigation id and role boundary`() {
        AppDestination.entries
            .filter { it.scope != com.daily.nexamartpartner.routing.DestinationScope.PUBLIC }
            .forEach { destination ->
                assertTrue("Missing nav id for $destination", destination.navId != 0)
                when (destination.scope) {
                    com.daily.nexamartpartner.routing.DestinationScope.ADMIN -> {
                        assertTrue(AuthorizationPolicy.canAccess(UserRole.ADMIN, destination))
                        assertFalse(AuthorizationPolicy.canAccess(UserRole.DELIVERY_PARTNER, destination))
                    }
                    com.daily.nexamartpartner.routing.DestinationScope.DELIVERY -> {
                        assertTrue(AuthorizationPolicy.canAccess(UserRole.DELIVERY_PARTNER, destination))
                        assertFalse(AuthorizationPolicy.canAccess(UserRole.ADMIN, destination))
                    }
                    else -> Unit
                }
            }
    }

    @Test
    fun `destination lookup round trips known navigation ids`() {
        AppDestination.entries.forEach { destination ->
            assertNotEquals(null, AppDestination.fromNavId(destination.navId))
            assertTrue(AppDestination.fromNavId(destination.navId) == destination)
        }
    }

    @Test
    fun `currency formatter is deterministic for supported amount`() {
        val formatted = ValueFormatter.formatCurrency(BigDecimal("1234.50"), "INR")
        assertTrue(formatted.isNotBlank())
        assertTrue(formatted.contains("1,234"))
    }
}
