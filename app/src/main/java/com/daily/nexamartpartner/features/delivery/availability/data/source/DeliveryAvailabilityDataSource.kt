package com.daily.nexamartpartner.features.delivery.availability.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.availability.data.contract.DeliveryAvailabilityContract
import com.daily.nexamartpartner.features.delivery.availability.data.model.DeliveryAvailabilityDto
import com.daily.nexamartpartner.features.delivery.availability.domain.model.DeliveryAvailabilityUpdate

interface DeliveryAvailabilityDataSource {
    suspend fun get(): AppResult<DeliveryAvailabilityDto>
    suspend fun update(update: DeliveryAvailabilityUpdate): AppResult<DeliveryAvailabilityDto>
}

class DeliveryAvailabilityDataSourceImpl(
    private val api: DeliveryAvailabilityApi,
    private val contract: DeliveryAvailabilityContract,
    private val executor: ApiCallExecutor
) : DeliveryAvailabilityDataSource {
    override suspend fun get(): AppResult<DeliveryAvailabilityDto> {
        val path = contract.getPath ?: return AppResult.Failure(AppFailure("Delivery availability API contract is not configured.", type = FailureType.CONTRACT_MISSING))
        return executor.execute { api.get(path) }
    }
    override suspend fun update(update: DeliveryAvailabilityUpdate): AppResult<DeliveryAvailabilityDto> {
        val path = contract.updatePath ?: return AppResult.Failure(AppFailure("Delivery availability update API contract is not configured.", type = FailureType.CONTRACT_MISSING))
        val body = contract.buildUpdateBody(update) ?: return AppResult.Failure(AppFailure("Delivery availability update body contract is not configured.", type = FailureType.CONTRACT_MISSING))
        return executor.execute { api.update(path, body) }
    }
}
