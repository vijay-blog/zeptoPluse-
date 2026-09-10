package com.daily.nexamartpartner.features.delivery.profile.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.profile.data.contract.DeliveryPartnerProfileContract
import com.daily.nexamartpartner.features.delivery.profile.data.model.DeliveryPartnerProfileDto
import com.daily.nexamartpartner.features.delivery.profile.domain.model.DeliveryPartnerProfileUpdate

interface DeliveryPartnerProfileDataSource {
    suspend fun getProfile(): AppResult<DeliveryPartnerProfileDto>
    suspend fun updateProfile(update: DeliveryPartnerProfileUpdate): AppResult<DeliveryPartnerProfileDto>
}

class DeliveryPartnerProfileDataSourceImpl(
    private val api: DeliveryPartnerProfileApi,
    private val contract: DeliveryPartnerProfileContract,
    private val executor: ApiCallExecutor
) : DeliveryPartnerProfileDataSource {
    override suspend fun getProfile(): AppResult<DeliveryPartnerProfileDto> {
        val path = contract.profilePath ?: return AppResult.Failure(AppFailure("Delivery partner profile API contract is not configured.", type = FailureType.CONTRACT_MISSING))
        return executor.execute { api.getProfile(path) }
    }
    override suspend fun updateProfile(update: DeliveryPartnerProfileUpdate): AppResult<DeliveryPartnerProfileDto> {
        val path = contract.updatePath ?: return AppResult.Failure(AppFailure("Delivery partner profile update API contract is not configured.", type = FailureType.CONTRACT_MISSING))
        val body = contract.buildUpdateBody(update) ?: return AppResult.Failure(AppFailure("Delivery partner profile update body contract is not configured.", type = FailureType.CONTRACT_MISSING))
        return executor.execute { api.updateProfile(path, body) }
    }
}
