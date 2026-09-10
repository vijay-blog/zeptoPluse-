package com.daily.nexamartpartner.features.auth

import com.daily.nexamartpartner.features.auth.domain.model.SessionStatus
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import com.daily.nexamartpartner.features.auth.domain.session.SessionManager
import com.daily.nexamartpartner.testutil.InMemorySessionStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionManagerTest {
    @Test
    fun `save and read session`() = runTest {
        val storage = InMemorySessionStorage()
        val manager = SessionManager(storage)
        val session = testSession(UserRole.ADMIN)

        manager.saveSession(session)
        val restored = manager.initialize()

        assertEquals(SessionStatus.AUTHENTICATED, manager.sessionStatus.value)
        assertEquals(session, restored)
        assertEquals(UserRole.ADMIN, manager.getCurrentRole())
    }

    @Test
    fun `clear session resets state`() = runTest {
        val storage = InMemorySessionStorage()
        val manager = SessionManager(storage)
        manager.saveSession(testSession(UserRole.DELIVERY_PARTNER))

        manager.clearSession()

        assertEquals(SessionStatus.UNAUTHENTICATED, manager.sessionStatus.value)
        assertNull(manager.currentSession.value)
    }

    private fun testSession(role: UserRole): UserSession {
        return UserSession(
            accessToken = "access",
            refreshToken = "refresh",
            userId = 99L,
            name = "Test User",
            contact = "9999999999",
            role = role
        )
    }
}
