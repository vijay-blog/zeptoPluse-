package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.features.admin.domain.model.PartnerAccountStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAvailability
import com.daily.nexamartpartner.features.admin.domain.model.PartnerVerificationStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerWorkState
import org.junit.Assert.assertEquals
import org.junit.Test

class DeliveryPartnerStatusTest {
    @Test
    fun `backend statuses map centrally`() {
        assertEquals(PartnerAccountStatus.SUSPENDED, PartnerAccountStatus.fromRaw("SUSPENDED"))
        assertEquals(PartnerVerificationStatus.VERIFIED, PartnerVerificationStatus.fromRaw("VERIFIED"))
        assertEquals(PartnerAvailability.ONLINE, PartnerAvailability.fromRaw("ONLINE"))
        assertEquals(PartnerWorkState.BUSY, PartnerWorkState.fromRaw("BUSY"))
        assertEquals(PartnerAccountStatus.UNKNOWN, PartnerAccountStatus.fromRaw("unsupported"))
    }
}
