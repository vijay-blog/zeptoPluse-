package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.domain.model.AdminDashboard
import com.daily.nexamartpartner.features.admin.domain.model.DashboardKpis
import com.daily.nexamartpartner.features.admin.domain.model.RecentOrderSummary
import com.daily.nexamartpartner.features.admin.domain.repository.AdminDashboardRepository
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminDashboardUseCase
import com.daily.nexamartpartner.features.admin.presentation.state.AdminDashboardUiState
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.AdminDashboardViewModel
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import com.daily.nexamartpartner.features.auth.domain.session.SessionManager
import com.daily.nexamartpartner.testutil.InMemorySessionStorage
import com.daily.nexamartpartner.testutil.MainDispatcherRule
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminDashboardViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state starts loading`() = runTest {
        val viewModel = buildViewModel(
            mutableListOf(AppResult.Success(successDashboard()))
        )
        assertTrue(viewModel.uiState.value.content is AdminDashboardUiState.ContentState.Loading)
    }

    @Test
    fun `successful dashboard load emits success`() = runTest {
        val viewModel = buildViewModel(
            mutableListOf(AppResult.Success(successDashboard()))
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is AdminDashboardUiState.ContentState.Success)
    }

    @Test
    fun `empty response emits empty state`() = runTest {
        val viewModel = buildViewModel(
            mutableListOf(
                AppResult.Success(
                    AdminDashboard(
                        kpis = null,
                        recentOrders = emptyList(),
                        recentOrdersAvailable = true
                    )
                )
            )
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is AdminDashboardUiState.ContentState.Empty)
    }

    @Test
    fun `api error emits error state`() = runTest {
        val viewModel = buildViewModel(
            mutableListOf(
                AppResult.Failure(
                    AppFailure("Server failure", 500, FailureType.SERVER)
                )
            )
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.content is AdminDashboardUiState.ContentState.Error)
    }

    @Test
    fun `retry triggers a second request`() = runTest {
        val repository = FakeRepository(
            mutableListOf(
                AppResult.Failure(AppFailure("Server failure", 500, FailureType.SERVER)),
                AppResult.Success(successDashboard())
            )
        )
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.retry()
        advanceUntilIdle()

        assertEquals(2, repository.callCount)
        assertTrue(viewModel.uiState.value.content is AdminDashboardUiState.ContentState.Success)
    }

    @Test
    fun `refresh updates refreshing state and refetches data`() = runTest {
        val repository = FakeRepository(
            mutableListOf(
                AppResult.Success(successDashboard()),
                AppResult.Success(successDashboard(totalOrders = 20))
            )
        )
        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.refresh()
        assertTrue(viewModel.uiState.value.isRefreshing)
        advanceUntilIdle()

        assertEquals(2, repository.callCount)
        assertTrue(viewModel.uiState.value.content is AdminDashboardUiState.ContentState.Success)
    }

    @Test
    fun `authentication error emits error state`() = runTest {
        val viewModel = buildViewModel(
            mutableListOf(
                AppResult.Failure(
                    AppFailure("Session expired", 401, FailureType.UNAUTHORIZED)
                )
            )
        )

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.content is AdminDashboardUiState.ContentState.Error)
    }

    private suspend fun buildViewModel(
        results: MutableList<AppResult<AdminDashboard>> = mutableListOf(AppResult.Success(successDashboard())),
        repository: FakeRepository = FakeRepository(results)
    ): AdminDashboardViewModel {
        val sessionManager = SessionManager(InMemorySessionStorage())
        sessionManager.saveSession(
            UserSession(
                accessToken = "access",
                refreshToken = "refresh",
                userId = 1L,
                name = "Admin User",
                contact = "9999999999",
                role = UserRole.ADMIN
            )
        )
        return AdminDashboardViewModel(GetAdminDashboardUseCase(repository), sessionManager)
    }

    private fun successDashboard(totalOrders: Long = 10L): AdminDashboard {
        return AdminDashboard(
            kpis = DashboardKpis(
                totalOrders = totalOrders,
                todayOrders = 2L,
                pendingOrders = 1L,
                outForDelivery = 3L,
                deliveredToday = 4L,
                todaySales = BigDecimal("1250.50"),
                currencyCode = "INR"
            ),
            recentOrders = listOf(
                RecentOrderSummary(
                    orderId = "NM10001",
                    customerName = "Rahul",
                    amount = BigDecimal("850.00"),
                    currencyCode = "INR",
                    status = "OUT_FOR_DELIVERY",
                    createdAt = "2026-09-07T13:00:00Z"
                )
            ),
            recentOrdersAvailable = true
        )
    }

    private class FakeRepository(
        private val results: MutableList<AppResult<AdminDashboard>>
    ) : AdminDashboardRepository {
        var callCount: Int = 0

        override suspend fun getDashboard(): AppResult<AdminDashboard> {
            callCount += 1
            return if (results.isNotEmpty()) {
                results.removeAt(0)
            } else {
                AppResult.Success(
                    AdminDashboard(
                        kpis = null,
                        recentOrders = emptyList(),
                        recentOrdersAvailable = true
                    )
                )
            }
        }
    }
}
