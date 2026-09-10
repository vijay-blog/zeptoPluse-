package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.data.model.AdminDashboardResponseDto
import com.daily.nexamartpartner.features.admin.data.model.AdminRecentOrderDto
import com.daily.nexamartpartner.features.admin.data.repository.AdminDashboardRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.source.AdminDashboardRemoteDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdminDashboardRepositoryImplTest {
    @Test
    fun `successful response maps to domain model`() = runTest {
        val repository = AdminDashboardRepositoryImpl(
            remoteDataSource = FakeRemoteDataSource(
                AppResult.Success(
                    AdminDashboardResponseDto(
                        totalOrders = 10,
                        todayOrders = 2,
                        pendingOrders = 1,
                        outForDelivery = 3,
                        deliveredToday = 4,
                        todaySales = 1250.50,
                        currencyCode = "INR",
                        recentOrders = listOf(
                            AdminRecentOrderDto(
                                orderId = "NM10001",
                                customerName = "Rahul",
                                amount = 850.0,
                                status = "OUT_FOR_DELIVERY",
                                createdAt = "2026-09-07T13:00:00Z"
                            )
                        )
                    )
                )
            )
        )

        val result = repository.getDashboard()

        assertTrue(result is AppResult.Success)
        val dashboard = (result as AppResult.Success).data
        assertEquals(10L, dashboard.kpis?.totalOrders)
        assertEquals(1, dashboard.recentOrders.size)
        assertEquals("NM10001", dashboard.recentOrders.first().orderId)
    }

    @Test
    fun `api failure propagates`() = runTest {
        val repository = AdminDashboardRepositoryImpl(
            remoteDataSource = FakeRemoteDataSource(
                AppResult.Failure(
                    AppFailure(
                        message = "Unable to load dashboard",
                        code = 500,
                        type = FailureType.SERVER
                    )
                )
            )
        )

        val result = repository.getDashboard()

        assertTrue(result is AppResult.Failure)
        assertEquals(FailureType.SERVER, (result as AppResult.Failure).error.type)
    }

    @Test
    fun `network failure propagates`() = runTest {
        val repository = AdminDashboardRepositoryImpl(
            remoteDataSource = FakeRemoteDataSource(
                AppResult.Failure(
                    AppFailure(
                        message = "Unable to connect",
                        type = FailureType.NETWORK
                    )
                )
            )
        )

        val result = repository.getDashboard()

        assertTrue(result is AppResult.Failure)
        assertEquals(FailureType.NETWORK, (result as AppResult.Failure).error.type)
    }

    @Test
    fun `mapping failure returns server error`() = runTest {
        val repository = AdminDashboardRepositoryImpl(
            remoteDataSource = FakeRemoteDataSource(
                AppResult.Success(
                    AdminDashboardResponseDto(
                        totalOrders = 10,
                        todayOrders = 2,
                        pendingOrders = 1,
                        outForDelivery = 3,
                        deliveredToday = 4,
                        todaySales = 1250.50,
                        currencyCode = "INR",
                        recentOrders = listOf(
                            AdminRecentOrderDto(
                                orderId = null,
                                customerName = "Rahul",
                                amount = 850.0,
                                status = "OUT_FOR_DELIVERY",
                                createdAt = "2026-09-07T13:00:00Z"
                            )
                        )
                    )
                )
            )
        )

        val result = repository.getDashboard()

        assertTrue(result is AppResult.Failure)
        assertEquals(FailureType.SERVER, (result as AppResult.Failure).error.type)
    }

    private class FakeRemoteDataSource(
        private val result: AppResult<AdminDashboardResponseDto>
    ) : AdminDashboardRemoteDataSource {
        override suspend fun getDashboard(): AppResult<AdminDashboardResponseDto> = result
    }
}
