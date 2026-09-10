package com.daily.nexamartpartner.features.delivery.domain.usecase

import com.daily.nexamartpartner.features.delivery.domain.repository.DeliveryDashboardRepository

class GetDeliveryDashboardUseCase(private val repository: DeliveryDashboardRepository) {
    suspend operator fun invoke() = repository.getDashboard()
}
