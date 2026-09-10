package com.daily.nexamartpartner.features.admin.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerDetails
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnersQuery
import com.daily.nexamartpartner.features.admin.domain.model.PagedDeliveryPartners
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction

interface DeliveryPartnerRepository {
    suspend fun getPartners(query: DeliveryPartnersQuery): AppResult<PagedDeliveryPartners>
    suspend fun getPartnerDetails(partnerId: String): AppResult<DeliveryPartnerDetails>
    suspend fun performAction(partnerId: String, action: PartnerAdminAction, reason: String?): AppResult<Unit>
}
