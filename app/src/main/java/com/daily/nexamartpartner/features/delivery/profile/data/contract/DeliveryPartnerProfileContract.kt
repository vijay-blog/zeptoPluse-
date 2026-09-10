package com.daily.nexamartpartner.features.delivery.profile.data.contract

import com.daily.nexamartpartner.features.delivery.profile.domain.model.DeliveryPartnerProfileUpdate

interface DeliveryPartnerProfileContract {
    val profilePath: String?
    val updatePath: String?
    fun buildUpdateBody(update: DeliveryPartnerProfileUpdate): Any? = null
}

class PendingBackendDeliveryPartnerProfileContract : DeliveryPartnerProfileContract {
    override val profilePath: String? = null
    override val updatePath: String? = null
    override fun buildUpdateBody(update: DeliveryPartnerProfileUpdate): Any? = null
}
