package com.daily.nexamartpartner.features.delivery.earnings.data.model

import com.squareup.moshi.Json

data class DeliveryEarningsSummaryDto(
    @field:Json(name = "currencyCode") val currencyCode: String?,
    @field:Json(name = "today") val today: String?,
    @field:Json(name = "thisWeek") val thisWeek: String?,
    @field:Json(name = "thisMonth") val thisMonth: String?,
    @field:Json(name = "completedDeliveries") val completedDeliveries: Long?,
    @field:Json(name = "pendingPayout") val pendingPayout: String?,
    @field:Json(name = "totalEarned") val totalEarned: String?
)

data class DeliveryEarningEntryDto(
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "orderId") val orderId: String?,
    @field:Json(name = "earnedAt") val earnedAt: String?,
    @field:Json(name = "amount") val amount: String?,
    @field:Json(name = "currencyCode") val currencyCode: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "description") val description: String?
)

data class DeliveryEarningsPageDto(
    @field:Json(name = "content") val content: List<DeliveryEarningEntryDto>?,
    @field:Json(name = "number") val number: Int?,
    @field:Json(name = "size") val size: Int?,
    @field:Json(name = "totalPages") val totalPages: Int?,
    @field:Json(name = "totalElements") val totalElements: Long?,
    @field:Json(name = "last") val last: Boolean?
)
