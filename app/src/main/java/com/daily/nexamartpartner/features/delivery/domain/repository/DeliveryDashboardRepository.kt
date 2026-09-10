package com.daily.nexamartpartner.features.delivery.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryDashboard

interface DeliveryDashboardRepository {
    suspend fun getDashboard(): AppResult<DeliveryDashboard>
}
