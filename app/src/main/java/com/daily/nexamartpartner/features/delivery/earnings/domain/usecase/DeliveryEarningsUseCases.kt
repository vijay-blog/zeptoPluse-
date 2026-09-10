package com.daily.nexamartpartner.features.delivery.earnings.domain.usecase

import com.daily.nexamartpartner.features.delivery.earnings.domain.model.DeliveryEarningsQuery
import com.daily.nexamartpartner.features.delivery.earnings.domain.repository.DeliveryEarningsRepository

class GetDeliveryEarningsSummaryUseCase(private val repository: DeliveryEarningsRepository) { suspend operator fun invoke(query: DeliveryEarningsQuery) = repository.getSummary(query) }
class GetDeliveryEarningsHistoryUseCase(private val repository: DeliveryEarningsRepository) { suspend operator fun invoke(query: DeliveryEarningsQuery) = repository.getHistory(query) }
