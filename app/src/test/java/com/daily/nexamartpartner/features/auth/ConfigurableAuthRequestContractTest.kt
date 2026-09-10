package com.daily.nexamartpartner.features.auth

import com.daily.nexamartpartner.features.auth.data.contract.ConfigurableAuthRequestContract
import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials
import org.junit.Assert.assertEquals
import org.junit.Test

class ConfigurableAuthRequestContractTest {
    @Test fun `login uses email and password keys`() {
        assertEquals(mapOf("email" to "a@example.com", "password" to "secret123"), ConfigurableAuthRequestContract().buildLoginBody(LoginCredentials("a@example.com", "secret123")))
    }
}
