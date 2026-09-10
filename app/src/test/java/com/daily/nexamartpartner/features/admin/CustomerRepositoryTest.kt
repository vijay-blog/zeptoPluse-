package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.customer.data.model.*
import com.daily.nexamartpartner.features.admin.customer.data.repository.CustomerManagementRepositoryImpl
import com.daily.nexamartpartner.features.admin.customer.data.source.CustomerManagementRemoteDataSource
import com.daily.nexamartpartner.features.admin.customer.domain.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class CustomerRepositoryTest {
    @Test fun mapsCustomerResponseWithoutExposingSecrets() = runTest {
        val source = object : CustomerManagementRemoteDataSource {
            override suspend fun list(query: CustomerQuery) = AppResult.Success(CustomersPageDto(
                content = listOf(CustomerDto("c1", "Test User", "999", "x@y.com", null, "ACTIVE", "2026-01-01", null, 4, 1250.0, "INR", "Hyderabad", listOf("DEACTIVATE"))),
                page = 0, pageSize = 20, totalPages = 1, totalElements = 1, hasNextPage = false
            ))
            override suspend fun details(customerId: String) = error("not needed")
            override suspend fun action(customerId: String, action: CustomerAdminAction) = error("not needed")
        }
        val result = CustomerManagementRepositoryImpl(source).list(CustomerQuery(0,20,null,null))
        assertTrue(result is AppResult.Success)
        val customer = (result as AppResult.Success).data.customers.single()
        assertEquals("c1", customer.customerId)
        assertEquals(CustomerAccountStatus.ACTIVE, customer.accountStatus)
        assertEquals(4L, customer.orderCount)
        assertEquals(listOf(CustomerAdminAction.DEACTIVATE), customer.allowedActions)
    }
}
