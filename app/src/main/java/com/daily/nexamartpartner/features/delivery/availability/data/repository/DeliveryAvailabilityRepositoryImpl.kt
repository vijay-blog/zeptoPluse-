package com.daily.nexamartpartner.features.delivery.availability.data.repository

import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.availability.data.model.DeliveryAvailabilityDto
import com.daily.nexamartpartner.features.delivery.availability.data.source.DeliveryAvailabilityDataSource
import com.daily.nexamartpartner.features.delivery.availability.domain.model.*
import com.daily.nexamartpartner.features.delivery.availability.domain.repository.DeliveryAvailabilityRepository

class DeliveryAvailabilityRepositoryImpl(private val source: DeliveryAvailabilityDataSource) : DeliveryAvailabilityRepository {
    private fun map(d: DeliveryAvailabilityDto) = DeliveryAvailability(d.available, d.status?.trim()?.ifEmpty { null }, d.canChange == true, d.reason?.trim()?.ifEmpty { null }, d.updatedAt?.trim()?.ifEmpty { null })
    override suspend fun get(): AppResult<DeliveryAvailability> = when (val r = source.get()) { is AppResult.Success -> AppResult.Success(map(r.data)); is AppResult.Failure -> r }
    override suspend fun update(update: DeliveryAvailabilityUpdate): AppResult<DeliveryAvailability> = when (val r = source.update(update)) { is AppResult.Success -> AppResult.Success(map(r.data)); is AppResult.Failure -> r }
}
