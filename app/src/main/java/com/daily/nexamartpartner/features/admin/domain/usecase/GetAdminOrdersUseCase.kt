package com.daily.nexamartpartner.features.admin.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.PagedAdminOrders
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository

class GetAdminOrdersUseCase(
    private val repository: AdminOrdersRepository
) {
    suspend operator fun invoke(query: AdminOrdersQuery): AppResult<PagedAdminOrders> {
        return repository.getOrders(query)
    }
}
