package com.daily.nexamartpartner.features.delivery.availability.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.delivery.availability.domain.model.*

interface DeliveryAvailabilityRepository {
    suspend fun get(): AppResult<DeliveryAvailability>
    suspend fun update(update: DeliveryAvailabilityUpdate): AppResult<DeliveryAvailability>
}
