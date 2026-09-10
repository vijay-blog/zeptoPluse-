package com.daily.nexamartpartner.features.admin.data.model

import com.squareup.moshi.Json

data class DeliveryPartnersPageDto(
    @field:Json(name = "content") val content: List<DeliveryPartnerSummaryDto>?,
    @field:Json(name = "number") val number: Int?,
    @field:Json(name = "size") val size: Int?,
    @field:Json(name = "totalPages") val totalPages: Int?,
    @field:Json(name = "totalElements") val totalElements: Long?,
    @field:Json(name = "last") val last: Boolean?
)

data class DeliveryPartnerSummaryDto(
    @field:Json(name = "partnerId") val partnerId: String?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "phone") val phone: String?,
    @field:Json(name = "profileImageUrl") val profileImageUrl: String?,
    @field:Json(name = "accountStatus") val accountStatus: String?,
    @field:Json(name = "verificationStatus") val verificationStatus: String?,
    @field:Json(name = "availability") val availability: String?,
    @field:Json(name = "workState") val workState: String?,
    @field:Json(name = "activeDeliveries") val activeDeliveries: Int?,
    @field:Json(name = "isAssignable") val isAssignable: Boolean?
)

data class DeliveryPartnerDetailsDto(
    @field:Json(name = "partnerId") val partnerId: String?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "phone") val phone: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "profileImageUrl") val profileImageUrl: String?,
    @field:Json(name = "accountStatus") val accountStatus: String?,
    @field:Json(name = "verificationStatus") val verificationStatus: String?,
    @field:Json(name = "availability") val availability: String?,
    @field:Json(name = "workState") val workState: String?,
    @field:Json(name = "registeredAt") val registeredAt: String?,
    @field:Json(name = "lastActiveAt") val lastActiveAt: String?,
    @field:Json(name = "vehicleType") val vehicleType: String?,
    @field:Json(name = "vehicleNumber") val vehicleNumber: String?,
    @field:Json(name = "licenseReference") val licenseReference: String?,
    @field:Json(name = "statistics") val statistics: DeliveryPartnerStatisticsDto?,
    @field:Json(name = "currentOrders") val currentOrders: List<PartnerOrderSummaryDto>?,
    @field:Json(name = "recentHistory") val recentHistory: List<PartnerOrderSummaryDto>?,
    @field:Json(name = "isAssignable") val isAssignable: Boolean?,
    @field:Json(name = "allowedActions") val allowedActions: List<String>?
)

data class DeliveryPartnerStatisticsDto(
    @field:Json(name = "totalDeliveries") val totalDeliveries: Long?,
    @field:Json(name = "completedDeliveries") val completedDeliveries: Long?,
    @field:Json(name = "cancelledDeliveries") val cancelledDeliveries: Long?,
    @field:Json(name = "activeDeliveries") val activeDeliveries: Long?
)

data class PartnerOrderSummaryDto(
    @field:Json(name = "orderId") val orderId: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "timestamp") val timestamp: String?
)
