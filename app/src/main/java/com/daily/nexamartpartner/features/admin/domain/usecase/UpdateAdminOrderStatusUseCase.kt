package com.daily.nexamartpartner.features.admin.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository

class UpdateAdminOrderStatusUseCase(
    private val repository: AdminOrdersRepository
) {
    suspend operator fun invoke(orderId: String, status: OrderStatus): AppResult<Unit> {
        return repository.updateOrderStatus(orderId, status)
    }
}
