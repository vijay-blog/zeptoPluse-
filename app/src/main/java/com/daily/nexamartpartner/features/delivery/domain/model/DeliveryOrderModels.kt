package com.daily.nexamartpartner.features.delivery.domain.model

import java.math.BigDecimal

data class DeliveryOrdersQuery(
    val page: Int = 0,
    val pageSize: Int = 20,
    val searchText: String? = null,
    val status: String? = null,
    val fromDate: String? = null,
    val toDate: String? = null
)

data class PagedDeliveryOrders(
    val orders: List<DeliveryOrderSummary>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNextPage: Boolean
)

data class DeliveryOrderSummary(
    val orderId: String,
    val customerName: String,
    val customerPhone: String?,
    val address: String?,
    val totalAmount: BigDecimal?,
    val currencyCode: String?,
    val status: String,
    val paymentStatus: String?,
    val assignedAt: String?,
    val createdAt: String?
)

data class DeliveryOrderDetails(
    val orderId: String,
    val customerName: String,
    val customerPhone: String?,
    val address: String?,
    val totalAmount: BigDecimal?,
    val currencyCode: String?,
    val status: String,
    val paymentStatus: String?,
    val items: List<DeliveryOrderItem>,
    val assignedAt: String?,
    val createdAt: String?,
    val timeline: List<DeliveryOrderTimeline>,
    val allowedActions: List<DeliveryOrderAction>,
    val proofOfDeliveryRequired: Boolean = false,
    val proofOfDeliveryStatus: String? = null,
    val proofOfDeliveryUrl: String? = null
)

data class DeliveryOrderItem(
    val productName: String,
    val quantity: Int,
    val unitPrice: BigDecimal?,
    val lineTotal: BigDecimal?
)

data class DeliveryOrderTimeline(val status: String, val timestamp: String?)

enum class DeliveryOrderAction(val backendValue: String, val label: String) {
    ACCEPT("ACCEPT", "Accept Order"),
    REJECT("REJECT", "Reject Order"),
    PICKUP("PICKUP", "Mark Picked Up"),
    OUT_FOR_DELIVERY("OUT_FOR_DELIVERY", "Start Delivery"),
    COMPLETE("COMPLETE", "Mark Delivered"),
    CANCEL("CANCEL", "Cancel Delivery");

    companion object {
        fun fromBackend(value: String): DeliveryOrderAction? = entries.firstOrNull { it.backendValue.equals(value.trim(), true) }
    }
}
