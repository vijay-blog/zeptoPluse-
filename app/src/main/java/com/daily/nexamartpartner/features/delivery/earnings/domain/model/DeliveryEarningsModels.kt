package com.daily.nexamartpartner.features.delivery.earnings.domain.model

import java.math.BigDecimal

data class DeliveryEarningsQuery(
    val page: Int = 0,
    val pageSize: Int = 20,
    val fromDate: String? = null,
    val toDate: String? = null
)

data class DeliveryEarningsSummary(
    val currencyCode: String?,
    val today: BigDecimal?,
    val thisWeek: BigDecimal?,
    val thisMonth: BigDecimal?,
    val completedDeliveries: Long?,
    val pendingPayout: BigDecimal?,
    val totalEarned: BigDecimal?
)

data class DeliveryEarningEntry(
    val id: String,
    val orderId: String?,
    val earnedAt: String?,
    val amount: BigDecimal?,
    val currencyCode: String?,
    val status: String?,
    val description: String?
)

data class PagedDeliveryEarnings(
    val entries: List<DeliveryEarningEntry>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNextPage: Boolean
)
