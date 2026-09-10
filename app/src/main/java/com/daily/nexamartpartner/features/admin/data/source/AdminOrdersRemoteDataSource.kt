package com.daily.nexamartpartner.features.admin.data.source

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.data.model.AdminOrderDetailsDto
import com.daily.nexamartpartner.features.admin.data.model.AdminOrdersPageDto
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus

interface AdminOrdersRemoteDataSource {
    suspend fun getOrders(query: AdminOrdersQuery): AppResult<AdminOrdersPageDto>
    suspend fun getOrderDetails(orderId: String): AppResult<AdminOrderDetailsDto>
    suspend fun updateOrderStatus(orderId: String, targetStatus: OrderStatus): AppResult<Unit>
    suspend fun cancelOrder(orderId: String, reason: String?): AppResult<Unit>
}
