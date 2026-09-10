package com.daily.nexamartpartner.features.admin.customer.data.repository

import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.admin.customer.data.model.*
import com.daily.nexamartpartner.features.admin.customer.data.source.CustomerManagementRemoteDataSource
import com.daily.nexamartpartner.features.admin.customer.domain.model.*
import com.daily.nexamartpartner.features.admin.customer.domain.repository.CustomerManagementRepository

class CustomerManagementRepositoryImpl(private val remote: CustomerManagementRemoteDataSource) : CustomerManagementRepository {
    override suspend fun list(query: CustomerQuery): AppResult<PagedCustomers> = when (val r = remote.list(query)) {
        is AppResult.Failure -> r
        is AppResult.Success -> AppResult.Success(PagedCustomers(
            customers = r.data.content.orEmpty().mapNotNull(::map),
            page = r.data.page ?: query.page,
            pageSize = r.data.pageSize ?: query.pageSize,
            totalPages = r.data.totalPages ?: 0,
            totalElements = r.data.totalElements ?: 0L,
            hasNextPage = r.data.hasNextPage ?: false
        ))
    }

    override suspend fun details(customerId: String): AppResult<Customer> = when (val r = remote.details(customerId)) {
        is AppResult.Failure -> r
        is AppResult.Success -> map(r.data)?.let { AppResult.Success(it) } ?: invalid("Invalid customer details response.")
    }

    override suspend fun action(customerId: String, action: CustomerAdminAction): AppResult<Unit> = remote.action(customerId, action)

    private fun map(d: CustomerDto): Customer? {
        val id = d.customerId?.trim().takeUnless { it.isNullOrEmpty() } ?: return null
        return Customer(
            customerId = id,
            name = d.name?.trim().orEmpty(),
            phone = d.phone?.trim(),
            email = d.email?.trim(),
            profileImageUrl = d.profileImageUrl?.trim(),
            accountStatus = d.accountStatus?.let(CustomerAccountStatus::fromRaw),
            registeredAt = d.registeredAt?.trim(),
            lastActiveAt = d.lastActiveAt?.trim(),
            orderCount = d.orderCount,
            totalSpent = d.totalSpent,
            currencyCode = d.currencyCode?.trim(),
            defaultAddress = d.defaultAddress?.trim(),
            allowedActions = d.allowedActions.orEmpty().mapNotNull { raw -> CustomerAdminAction.entries.firstOrNull { it.backendValue.equals(raw.trim(), true) } }
        )
    }

    private fun <T> invalid(message: String): AppResult<T> = AppResult.Failure(AppFailure(message, type = FailureType.SERVER))
}
