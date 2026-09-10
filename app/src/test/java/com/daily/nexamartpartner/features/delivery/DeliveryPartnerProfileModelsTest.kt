package com.daily.nexamartpartner.features.delivery

import com.daily.nexamartpartner.features.delivery.profile.domain.model.DeliveryPartnerProfile
import com.daily.nexamartpartner.features.delivery.profile.domain.model.DeliveryPartnerProfileUpdate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeliveryPartnerProfileModelsTest {
    @Test fun editableFields_areBackendControlled() {
        val p = DeliveryPartnerProfile(1,"Partner","999",null,null,"VERIFIED","ACTIVE","Bike","TS01","DL01",null,null,setOf("name"))
        assertTrue(p.editableFields.contains("name"))
        assertTrue(!p.editableFields.contains("phone"))
    }

    @Test fun updateModel_doesNotContainSecuritySecrets() {
        val u = DeliveryPartnerProfileUpdate("Partner", "p@example.com", "Bike", "TS01", "DL01")
        assertEquals("Partner", u.name)
        assertEquals("p@example.com", u.email)
    }
}
