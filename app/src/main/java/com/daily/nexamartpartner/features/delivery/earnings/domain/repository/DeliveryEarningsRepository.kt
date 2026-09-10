package com.daily.nexamartpartner.features.delivery.earnings.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.*

interface DeliveryEarningsRepository {
    suspend fun getSummary(query: DeliveryEarningsQuery): AppResult<DeliveryEarningsSummary>
    suspend fun getHistory(query: DeliveryEarningsQuery): AppResult<PagedDeliveryEarnings>
}
