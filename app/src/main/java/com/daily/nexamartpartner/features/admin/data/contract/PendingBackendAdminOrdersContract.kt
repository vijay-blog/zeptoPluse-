package com.daily.nexamartpartner.features.admin.data.contract

import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus

class PendingBackendAdminOrdersContract : AdminOrdersContract {
    override val listOrdersPath: String? = null
    override val orderDetailsPathTemplate: String? = null
    override val updateStatusPathTemplate: String? = null
    override val cancelOrderPathTemplate: String? = null
    override val assignDeliveryPathTemplate: String? = null

    override fun buildOrderListQuery(query: AdminOrdersQuery): Map<String, String>? = null

    override fun buildUpdateStatusBody(status: OrderStatus): Map<String, String>? = null

    override fun buildCancelOrderBody(reason: String?): Map<String, String>? = null

    override fun resolvePath(template: String?, orderId: String): String? = null
}
