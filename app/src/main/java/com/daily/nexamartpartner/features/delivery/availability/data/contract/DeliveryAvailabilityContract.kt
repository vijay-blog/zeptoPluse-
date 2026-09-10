package com.daily.nexamartpartner.features.delivery.availability.data.contract

import com.daily.nexamartpartner.features.delivery.availability.domain.model.DeliveryAvailabilityUpdate

interface DeliveryAvailabilityContract {
    val getPath: String?
    val updatePath: String?
    fun buildUpdateBody(update: DeliveryAvailabilityUpdate): Any? = null
}

/** Activated only after the real Spring Boot endpoint and request field are confirmed. */
class PendingBackendDeliveryAvailabilityContract : DeliveryAvailabilityContract {
    override val getPath: String? = null
    override val updatePath: String? = null
    override fun buildUpdateBody(update: DeliveryAvailabilityUpdate): Any? = null
}
