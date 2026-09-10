package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderFilters
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSort
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSummary
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.model.PagedAdminOrders
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrdersUseCase
import com.daily.nexamartpartner.features.admin.presentation.state.AdminOrdersUiState
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.AdminOrdersViewModel
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
class AdminOrdersViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial loading then success`() = runTest {
        val vm = buildVm(
            mutableListOf(AppResult.Success(page(orders = listOf(order("NM1")))))
        )
        assertTrue(vm.uiState.value.content is AdminOrdersUiState.ContentState.Loading)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.content is AdminOrdersUiState.ContentState.Success)
    }

    @Test
    fun `error state on failure`() = runTest {
        val vm = buildVm(
            mutableListOf(AppResult.Failure(AppFailure("Server", 500, FailureType.SERVER)))
        )
        advanceUntilIdle()
        assertTrue(vm.uiState.value.content is AdminOrdersUiState.ContentState.Error)
    }

    @Test
    fun `search triggers reload`() = runTest {
        val repo = FakeOrdersRepository(
            mutableListOf(
                AppResult.Success(page(orders = listOf(order("NM1")))),
                AppResult.Success(page(orders = listOf(order("NM2"))))
            )
        )
        val vm = AdminOrdersViewModel(GetAdminOrdersUseCase(repo))
        advanceUntilIdle()
        vm.onSearchQueryChanged("NM2")
        advanceUntilIdle()
        assertEquals(2, repo.calls.size)
    }

    @Test
    fun `filter triggers reload`() = runTest {
        val repo = FakeOrdersRepository(
            mutableListOf(
                AppResult.Success(page(orders = listOf(order("NM1")))),
                AppResult.Success(page(orders = listOf(order("NM2", status = OrderStatus.READY))))
            )
        )
        val vm = AdminOrdersViewModel(GetAdminOrdersUseCase(repo))
        advanceUntilIdle()
        vm.onStatusFilterSelected(OrderStatus.READY)
        advanceUntilIdle()
        assertEquals(2, repo.calls.size)
        assertEquals(OrderStatus.READY, vm.uiState.value.selectedStatusFilter)
    }

    @Test
    fun `refresh keeps success flow`() = runTest {
        val repo = FakeOrdersRepository(
            mutableListOf(
                AppResult.Success(page(orders = listOf(order("NM1")))),
                AppResult.Success(page(orders = listOf(order("NM3"))))
            )
        )
        val vm = AdminOrdersViewModel(GetAdminOrdersUseCase(repo))
        advanceUntilIdle()
        vm.refresh()
        advanceUntilIdle()
        assertEquals(2, repo.calls.size)
        assertTrue(vm.uiState.value.content is AdminOrdersUiState.ContentState.Success)
    }

    @Test
    fun `pagination loads next page`() = runTest {
        val repo = FakeOrdersRepository(
            mutableListOf(
                AppResult.Success(page(orders = listOf(order("NM1")), page = 0, hasNext = true)),
                AppResult.Success(page(orders = listOf(order("NM2")), page = 1, hasNext = false))
            )
        )
        val vm = AdminOrdersViewModel(GetAdminOrdersUseCase(repo))
        advanceUntilIdle()
        vm.loadNextPage()
        advanceUntilIdle()
        assertEquals(2, repo.calls.size)
        val state = vm.uiState.value.content as AdminOrdersUiState.ContentState.Success
        assertEquals(2, state.orders.size)
    }

    private fun buildVm(results: MutableList<AppResult<PagedAdminOrders>>): AdminOrdersViewModel {
        return AdminOrdersViewModel(GetAdminOrdersUseCase(FakeOrdersRepository(results)))
    }

    private fun page(
        orders: List<AdminOrderSummary>,
        page: Int = 0,
        hasNext: Boolean = false
    ): PagedAdminOrders {
        return PagedAdminOrders(
            orders = orders,
            page = page,
            pageSize = 20,
            totalPages = if (hasNext) 2 else 1,
            totalElements = orders.size.toLong(),
            hasNextPage = hasNext
        )
    }

    private fun order(id: String, status: OrderStatus = OrderStatus.PENDING): AdminOrderSummary {
        return AdminOrderSummary(
            orderId = id,
            customerName = "Customer",
            customerPhone = "9999999999",
            itemCount = 2,
            totalAmount = BigDecimal("100.00"),
            currencyCode = "INR",
            orderStatus = status,
            paymentStatus = com.daily.nexamartpartner.features.admin.domain.model.PaymentStatus.PENDING,
            deliveryStatus = null,
            createdAt = "2026-09-07T10:00:00Z"
        )
    }

    private class FakeOrdersRepository(
        private val results: MutableList<AppResult<PagedAdminOrders>>
    ) : AdminOrdersRepository {
        val calls = mutableListOf<AdminOrdersQuery>()

        override suspend fun getOrders(query: AdminOrdersQuery): AppResult<PagedAdminOrders> {
            calls.add(query)
            return if (results.isNotEmpty()) {
                results.removeAt(0)
            } else {
                AppResult.Success(
                    PagedAdminOrders(
                        orders = emptyList(),
                        page = 0,
                        pageSize = 20,
                        totalPages = 1,
                        totalElements = 0,
                        hasNextPage = false
                    )
                )
            }
        }

        override suspend fun getOrderDetails(orderId: String) = AppResult.Failure(
            AppFailure("Not used", type = FailureType.UNKNOWN)
        )

        override suspend fun updateOrderStatus(orderId: String, status: OrderStatus) = AppResult.Success(Unit)

        override suspend fun cancelOrder(orderId: String, reason: String?) = AppResult.Success(Unit)
    }
}
