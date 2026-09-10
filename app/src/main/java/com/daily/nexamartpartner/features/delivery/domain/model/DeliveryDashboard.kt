package com.daily.nexamartpartner.features.delivery.domain.model

import java.math.BigDecimal

data class DeliveryDashboard(
    val activeOrders: Long?,
    val assignedOrders: Long?,
    val pickedUpOrders: Long?,
    val outForDeliveryOrders: Long?,
    val completedToday: Long?,
    val todayEarnings: BigDecimal?,
    val currencyCode: String?,
    val availability: String?,
    val recentOrders: List<DeliveryOrderSummary>,
    val recentOrdersAvailable: Boolean
) {
    val hasMetrics: Boolean
        get() = listOf(activeOrders, assignedOrders, pickedUpOrders, outForDeliveryOrders, completedToday, todayEarnings).any { it != null }
    val hasData: Boolean
        get() = hasMetrics || availability != null || recentOrders.isNotEmpty()
}
