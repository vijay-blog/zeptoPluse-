package com.daily.nexamartpartner.features.admin.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerDetails
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnersQuery
import com.daily.nexamartpartner.features.admin.domain.model.PagedDeliveryPartners
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction
import com.daily.nexamartpartner.features.admin.domain.repository.DeliveryPartnerRepository

class GetDeliveryPartnersUseCase(private val repository: DeliveryPartnerRepository) {
    suspend operator fun invoke(query: DeliveryPartnersQuery): AppResult<PagedDeliveryPartners> =
        repository.getPartners(query)
}

class GetDeliveryPartnerDetailsUseCase(private val repository: DeliveryPartnerRepository) {
    suspend operator fun invoke(partnerId: String): AppResult<DeliveryPartnerDetails> =
        repository.getPartnerDetails(partnerId)
}

class UpdateDeliveryPartnerUseCase(private val repository: DeliveryPartnerRepository) {
    suspend operator fun invoke(
        partnerId: String,
        action: PartnerAdminAction,
        reason: String?
    ): AppResult<Unit> = repository.performAction(partnerId, action, reason)
}
