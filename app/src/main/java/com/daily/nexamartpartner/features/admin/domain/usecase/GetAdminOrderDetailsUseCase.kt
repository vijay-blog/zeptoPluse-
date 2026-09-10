package com.daily.nexamartpartner.features.admin.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderDetails
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository

class GetAdminOrderDetailsUseCase(
    private val repository: AdminOrdersRepository
) {
    suspend operator fun invoke(orderId: String): AppResult<AdminOrderDetails> {
        return repository.getOrderDetails(orderId)
    }
}
