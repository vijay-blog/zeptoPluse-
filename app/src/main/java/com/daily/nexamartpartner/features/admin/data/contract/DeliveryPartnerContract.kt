package com.daily.nexamartpartner.features.admin.data.contract

import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnersQuery
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction

interface DeliveryPartnerContract {
    val listPath: String?
    val detailsPathTemplate: String?
    val actionPathTemplate: String?
    fun buildListQuery(query: DeliveryPartnersQuery): Map<String, String>?
    fun buildActionBody(action: PartnerAdminAction, reason: String?): Map<String, String>?
    fun resolvePath(template: String?, partnerId: String): String?
}

class PendingBackendDeliveryPartnerContract : DeliveryPartnerContract {
    override val listPath: String? = null
    override val detailsPathTemplate: String? = null
    override val actionPathTemplate: String? = null
    override fun buildListQuery(query: DeliveryPartnersQuery): Map<String, String>? = null
    override fun buildActionBody(action: PartnerAdminAction, reason: String?): Map<String, String>? = null
    override fun resolvePath(template: String?, partnerId: String): String? = null
}
