package com.daily.nexamartpartner.features.auth

import com.daily.nexamartpartner.features.auth.data.contract.RegistrationRequestContract
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationCredentials
import org.junit.Assert.assertEquals
import org.junit.Test

class RegistrationRequestContractTest {
    @Test fun `registration uses backend contract fields`() {
        val result = RegistrationRequestContract().buildBody(RegistrationCredentials(" Raghu ", "RAGHU@EXAMPLE.COM", "password123"))
        assertEquals("Raghu", result["name"])
        assertEquals("raghu@example.com", result["email"])
        assertEquals("password123", result["password"])
    }
}
