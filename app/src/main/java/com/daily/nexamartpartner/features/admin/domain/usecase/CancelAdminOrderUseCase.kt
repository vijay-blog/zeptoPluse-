package com.daily.nexamartpartner.features.admin.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository

class CancelAdminOrderUseCase(
    private val repository: AdminOrdersRepository
) {
    suspend operator fun invoke(orderId: String, reason: String?): AppResult<Unit> {
        return repository.cancelOrder(orderId, reason)
    }
}
