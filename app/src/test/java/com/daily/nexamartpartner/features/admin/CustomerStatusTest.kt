package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.features.admin.customer.domain.model.CustomerAccountStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomerStatusTest {
    @Test fun parsesKnownStatusesCaseInsensitively() {
        assertEquals(CustomerAccountStatus.ACTIVE, CustomerAccountStatus.fromRaw("active"))
        assertEquals(CustomerAccountStatus.SUSPENDED, CustomerAccountStatus.fromRaw(" SUSPENDED "))
    }

    @Test fun unknownStatusDoesNotCrash() {
        assertEquals(CustomerAccountStatus.UNKNOWN, CustomerAccountStatus.fromRaw("future_status"))
        assertEquals(CustomerAccountStatus.UNKNOWN, CustomerAccountStatus.fromRaw(null))
    }
}
