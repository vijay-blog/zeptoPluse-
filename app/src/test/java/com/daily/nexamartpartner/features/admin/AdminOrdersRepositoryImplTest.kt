package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.data.model.AdminOrderDetailsDto
import com.daily.nexamartpartner.features.admin.data.model.AdminOrderSummaryDto
import com.daily.nexamartpartner.features.admin.data.model.AdminOrdersPageDto
import com.daily.nexamartpartner.features.admin.data.repository.AdminOrdersRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.source.AdminOrdersRemoteDataSource
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderFilters
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSort
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminOrdersRepositoryImplTest {
    @Test
    fun `orders success maps content`() = runTest {
        val repository = AdminOrdersRepositoryImpl(
            remoteDataSource = FakeRemote(
                listResult = AppResult.Success(
                    AdminOrdersPageDto(
                        content = listOf(
                            AdminOrderSummaryDto(
                                orderId = "NM1001",
                                customerName = "Rahul",
                                customerPhone = "9999999999",
                                itemCount = 2,
                                totalAmount = "850.00",
                                currencyCode = "INR",
                                status = "PENDING",
                                paymentStatus = "PENDING",
                                deliveryStatus = null,
                                createdAt = "2026-09-07T10:00:00Z"
                            )
                        ),
                        number = 0,
                        size = 20,
                        totalPages = 1,
                        totalElements = 1,
                        last = true
                    )
                )
            )
        )

        val result = repository.getOrders(defaultQuery())
        assertTrue(result is AppResult.Success)
        val page = (result as AppResult.Success).data
        assertEquals(1, page.orders.size)
        assertEquals("NM1001", page.orders.first().orderId)
        assertEquals(OrderStatus.PENDING, page.orders.first().orderStatus)
    }

    @Test
    fun `orders empty maps empty list`() = runTest {
        val repository = AdminOrdersRepositoryImpl(
            remoteDataSource = FakeRemote(
                listResult = AppResult.Success(
                    AdminOrdersPageDto(
                        content = emptyList(),
                        number = 0,
                        size = 20,
                        totalPages = 0,
                        totalElements = 0,
                        last = true
                    )
                )
            )
        )
        val result = repository.getOrders(defaultQuery())
        assertTrue(result is AppResult.Success)
        assertEquals(0, (result as AppResult.Success).data.orders.size)
    }

    @Test
    fun `network failure propagates`() = runTest {
        val repository = AdminOrdersRepositoryImpl(
            remoteDataSource = FakeRemote(
                listResult = AppResult.Failure(AppFailure("Network", type = FailureType.NETWORK))
            )
        )
        val result = repository.getOrders(defaultQuery())
        assertTrue(result is AppResult.Failure)
        assertEquals(FailureType.NETWORK, (result as AppResult.Failure).error.type)
    }

    @Test
    fun `unauthorized failure propagates`() = runTest {
        val repository = AdminOrdersRepositoryImpl(
            remoteDataSource = FakeRemote(
                listResult = AppResult.Failure(AppFailure("Unauthorized", 401, FailureType.UNAUTHORIZED))
            )
        )
        val result = repository.getOrders(defaultQuery())
        assertTrue(result is AppResult.Failure)
        assertEquals(FailureType.UNAUTHORIZED, (result as AppResult.Failure).error.type)
    }

    @Test
    fun `forbidden failure propagates`() = runTest {
        val repository = AdminOrdersRepositoryImpl(
            remoteDataSource = FakeRemote(
                listResult = AppResult.Failure(AppFailure("Forbidden", 403, FailureType.FORBIDDEN))
            )
        )
        val result = repository.getOrders(defaultQuery())
        assertTrue(result is AppResult.Failure)
        assertEquals(FailureType.FORBIDDEN, (result as AppResult.Failure).error.type)
    }

    @Test
    fun `not found failure propagates`() = runTest {
        val repository = AdminOrdersRepositoryImpl(
            remoteDataSource = FakeRemote(
                detailsResult = AppResult.Failure(AppFailure("Order not found", 404, FailureType.UNKNOWN))
            )
        )
        val result = repository.getOrderDetails("NM404")
        assertTrue(result is AppResult.Failure)
        assertEquals(404, (result as AppResult.Failure).error.code)
    }

    @Test
    fun `conflict failure propagates`() = runTest {
        val repository = AdminOrdersRepositoryImpl(
            remoteDataSource = FakeRemote(
                updateResult = AppResult.Failure(AppFailure("Conflict", 409, FailureType.UNKNOWN))
            )
        )
        val result = repository.updateOrderStatus("NM1001", OrderStatus.READY)
        assertTrue(result is AppResult.Failure)
        assertEquals(409, (result as AppResult.Failure).error.code)
    }

    private fun defaultQuery() = AdminOrdersQuery(
        page = 0,
        pageSize = 20,
        searchText = null,
        filters = AdminOrderFilters(),
        sort = AdminOrderSort.NEWEST
    )

    private class FakeRemote(
        private val listResult: AppResult<AdminOrdersPageDto> = AppResult.Success(
            AdminOrdersPageDto(emptyList(), 0, 20, 0, 0, true)
        ),
        private val detailsResult: AppResult<AdminOrderDetailsDto> = AppResult.Failure(
            AppFailure("Not implemented", type = FailureType.UNKNOWN)
        ),
        private val updateResult: AppResult<Unit> = AppResult.Success(Unit),
        private val cancelResult: AppResult<Unit> = AppResult.Success(Unit)
    ) : AdminOrdersRemoteDataSource {
        override suspend fun getOrders(query: AdminOrdersQuery): AppResult<AdminOrdersPageDto> = listResult
        override suspend fun getOrderDetails(orderId: String): AppResult<AdminOrderDetailsDto> = detailsResult
        override suspend fun updateOrderStatus(orderId: String, targetStatus: OrderStatus): AppResult<Unit> = updateResult
        override suspend fun cancelOrder(orderId: String, reason: String?): AppResult<Unit> = cancelResult
    }
}
