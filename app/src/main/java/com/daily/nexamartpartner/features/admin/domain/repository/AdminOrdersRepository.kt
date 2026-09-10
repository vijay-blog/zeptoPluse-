package com.daily.nexamartpartner.features.admin.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderDetails
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.model.PagedAdminOrders

interface AdminOrdersRepository {
    suspend fun getOrders(query: AdminOrdersQuery): AppResult<PagedAdminOrders>
    suspend fun getOrderDetails(orderId: String): AppResult<AdminOrderDetails>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): AppResult<Unit>
    suspend fun cancelOrder(orderId: String, reason: String?): AppResult<Unit>
}
