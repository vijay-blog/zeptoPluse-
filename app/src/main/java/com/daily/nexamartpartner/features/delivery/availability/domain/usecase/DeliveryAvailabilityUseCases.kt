package com.daily.nexamartpartner.features.delivery.availability.domain.usecase

import com.daily.nexamartpartner.features.delivery.availability.domain.model.DeliveryAvailabilityUpdate
import com.daily.nexamartpartner.features.delivery.availability.domain.repository.DeliveryAvailabilityRepository

class GetDeliveryAvailabilityUseCase(private val repository: DeliveryAvailabilityRepository) { suspend operator fun invoke() = repository.get() }
class UpdateDeliveryAvailabilityUseCase(private val repository: DeliveryAvailabilityRepository) { suspend operator fun invoke(update: DeliveryAvailabilityUpdate) = repository.update(update) }
