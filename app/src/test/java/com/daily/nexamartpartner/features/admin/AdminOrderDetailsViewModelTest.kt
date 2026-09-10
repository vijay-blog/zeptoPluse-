package com.daily.nexamartpartner.features.admin

import androidx.lifecycle.SavedStateHandle
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderDetails
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderFilters
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSort
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSummary
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryInfo
import com.daily.nexamartpartner.features.admin.domain.model.OrderCustomer
import com.daily.nexamartpartner.features.admin.domain.model.OrderItem
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.model.OrderTimelineEntry
import com.daily.nexamartpartner.features.admin.domain.model.OrderTotals
import com.daily.nexamartpartner.features.admin.domain.model.PagedAdminOrders
import com.daily.nexamartpartner.features.admin.domain.model.PaymentInfo
import com.daily.nexamartpartner.features.admin.domain.model.PaymentMethod
import com.daily.nexamartpartner.features.admin.domain.model.PaymentStatus
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository
import com.daily.nexamartpartner.features.admin.domain.usecase.CancelAdminOrderUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrderDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateAdminOrderStatusUseCase
import com.daily.nexamartpartner.features.admin.presentation.state.AdminOrderDetailsUiState
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.AdminOrderDetailsViewModel
import com.daily.nexamartpartner.testutil.MainDispatcherRule
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminOrderDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `details initial load success`() = runTest {
        val repo = FakeRepository(
            detailsResults = mutableListOf(AppResult.Success(details()))
        )
        val vm = buildVm(repo)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.content is AdminOrderDetailsUiState.ContentState.Success)
    }

    @Test
    fun `details error on failure`() = runTest {
        val repo = FakeRepository(
            detailsResults = mutableListOf(
                AppResult.Failure(AppFailure("Server", 500, FailureType.SERVER))
            )
        )
        val vm = buildVm(repo)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.content is AdminOrderDetailsUiState.ContentState.Error)
    }

    @Test
    fun `status update triggers reload`() = runTest {
        val repo = FakeRepository(
            detailsResults = mutableListOf(
                AppResult.Success(details()),
                AppResult.Success(details(status = OrderStatus.READY))
            ),
            updateResult = AppResult.Success(Unit)
        )
        val vm = buildVm(repo)
        advanceUntilIdle()
        vm.updateStatus(OrderStatus.READY)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.content is AdminOrderDetailsUiState.ContentState.Success)
    }

    @Test
    fun `cancel triggers reload`() = runTest {
        val repo = FakeRepository(
            detailsResults = mutableListOf(
                AppResult.Success(details(canCancel = true)),
                AppResult.Success(details(status = OrderStatus.CANCELLED, canCancel = false))
            ),
            cancelResult = AppResult.Success(Unit)
        )
        val vm = buildVm(repo)
        advanceUntilIdle()
        vm.cancelOrder(null)
        advanceUntilIdle()
        assertTrue(vm.uiState.value.content is AdminOrderDetailsUiState.ContentState.Success)
    }

    private fun buildVm(repository: FakeRepository): AdminOrderDetailsViewModel {
        return AdminOrderDetailsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("orderId" to "NM1001")),
            getAdminOrderDetailsUseCase = GetAdminOrderDetailsUseCase(repository),
            updateAdminOrderStatusUseCase = UpdateAdminOrderStatusUseCase(repository),
            cancelAdminOrderUseCase = CancelAdminOrderUseCase(repository)
        )
    }

    private fun details(
        status: OrderStatus = OrderStatus.PENDING,
        canCancel: Boolean = true
    ): AdminOrderDetails {
        return AdminOrderDetails(
            orderId = "NM1001",
            createdAt = "2026-09-07T10:00:00Z",
            currentStatus = status,
            customer = OrderCustomer("Rahul", "9999999999", "Chennai"),
            items = listOf(
                OrderItem(
                    productName = "Rice",
                    quantity = 1,
                    unitPrice = BigDecimal("100.00"),
                    lineTotal = BigDecimal("100.00"),
                    currencyCode = "INR"
                )
            ),
            payment = PaymentInfo(PaymentMethod.COD, PaymentStatus.PENDING, null),
            totals = OrderTotals(
                subtotal = BigDecimal("100.00"),
                deliveryFee = BigDecimal("20.00"),
                discount = BigDecimal("0.00"),
                tax = BigDecimal("5.00"),
                grandTotal = BigDecimal("125.00"),
                currencyCode = "INR"
            ),
            delivery = DeliveryInfo(status.backendValue, null, null),
            timeline = listOf(OrderTimelineEntry(status, null)),
            allowedTransitions = listOf(OrderStatus.CONFIRMED),
            canCancel = canCancel
        )
    }

    private class FakeRepository(
        private val detailsResults: MutableList<AppResult<AdminOrderDetails>>,
        private val updateResult: AppResult<Unit> = AppResult.Success(Unit),
        private val cancelResult: AppResult<Unit> = AppResult.Success(Unit)
    ) : AdminOrdersRepository {
        override suspend fun getOrders(query: AdminOrdersQuery): AppResult<PagedAdminOrders> {
            return AppResult.Success(
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

        override suspend fun getOrderDetails(orderId: String): AppResult<AdminOrderDetails> {
            return if (detailsResults.isNotEmpty()) detailsResults.removeAt(0)
            else AppResult.Failure(AppFailure("Missing", type = FailureType.UNKNOWN))
        }

        override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): AppResult<Unit> = updateResult

        override suspend fun cancelOrder(orderId: String, reason: String?): AppResult<Unit> = cancelResult
    }
}
