package com.daily.nexamartpartner.features.admin.customer.domain.model

data class Customer(
    val customerId: String,
    val name: String,
    val phone: String?,
    val email: String?,
    val profileImageUrl: String?,
    val accountStatus: CustomerAccountStatus?,
    val registeredAt: String?,
    val lastActiveAt: String?,
    val orderCount: Long?,
    val totalSpent: Double?,
    val currencyCode: String?,
    val defaultAddress: String?,
    val allowedActions: List<CustomerAdminAction>
)

enum class CustomerAccountStatus(val backendValue: String) {
    ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), SUSPENDED("SUSPENDED"), PENDING("PENDING"), UNKNOWN("UNKNOWN");
    companion object { fun fromRaw(raw: String?): CustomerAccountStatus = entries.firstOrNull { it.backendValue.equals(raw?.trim(), true) } ?: UNKNOWN }
}

enum class CustomerAdminAction(val backendValue: String) {
    ACTIVATE("ACTIVATE"), DEACTIVATE("DEACTIVATE"), SUSPEND("SUSPEND"), REACTIVATE("REACTIVATE")
}

data class CustomerQuery(
    val page: Int,
    val pageSize: Int,
    val search: String?,
    val status: CustomerAccountStatus?
)

data class PagedCustomers(
    val customers: List<Customer>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNextPage: Boolean
)
