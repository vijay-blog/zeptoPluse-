package com.daily.nexamartpartner.features.admin.customer.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.customer.domain.model.*

interface CustomerManagementRepository {
    suspend fun list(query: CustomerQuery): AppResult<PagedCustomers>
    suspend fun details(customerId: String): AppResult<Customer>
    suspend fun action(customerId: String, action: CustomerAdminAction): AppResult<Unit>
}
