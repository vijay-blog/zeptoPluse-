package com.daily.nexamartpartner.features.admin.data.contract

import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus

interface AdminOrdersContract {
    val listOrdersPath: String?
    val orderDetailsPathTemplate: String?
    val updateStatusPathTemplate: String?
    val cancelOrderPathTemplate: String?
    val assignDeliveryPathTemplate: String?

    fun buildOrderListQuery(query: AdminOrdersQuery): Map<String, String>?
    fun buildUpdateStatusBody(status: OrderStatus): Map<String, String>?
    fun buildCancelOrderBody(reason: String?): Map<String, String>?
    fun resolvePath(template: String?, orderId: String): String?
}
