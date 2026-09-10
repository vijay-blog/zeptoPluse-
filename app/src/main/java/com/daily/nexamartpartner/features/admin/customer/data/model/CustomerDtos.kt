package com.daily.nexamartpartner.features.admin.customer.data.model

import com.squareup.moshi.Json

data class CustomerDto(
    @Json(name = "customerId") val customerId: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "phone") val phone: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "profileImageUrl") val profileImageUrl: String?,
    @Json(name = "accountStatus") val accountStatus: String?,
    @Json(name = "registeredAt") val registeredAt: String?,
    @Json(name = "lastActiveAt") val lastActiveAt: String?,
    @Json(name = "orderCount") val orderCount: Long?,
    @Json(name = "totalSpent") val totalSpent: Double?,
    @Json(name = "currencyCode") val currencyCode: String?,
    @Json(name = "defaultAddress") val defaultAddress: String?,
    @Json(name = "allowedActions") val allowedActions: List<String>?
)

data class CustomersPageDto(
    @Json(name = "content") val content: List<CustomerDto>?,
    @Json(name = "page") val page: Int?,
    @Json(name = "pageSize") val pageSize: Int?,
    @Json(name = "totalPages") val totalPages: Int?,
    @Json(name = "totalElements") val totalElements: Long?,
    @Json(name = "hasNextPage") val hasNextPage: Boolean?
)
