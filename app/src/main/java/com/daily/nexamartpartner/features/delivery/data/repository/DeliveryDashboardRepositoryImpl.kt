package com.daily.nexamartpartner.features.delivery.data.repository

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.delivery.data.model.DeliveryDashboardResponseDto
import com.daily.nexamartpartner.features.delivery.data.source.DeliveryDashboardDataSource
import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryDashboard
import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrderSummary
import com.daily.nexamartpartner.features.delivery.domain.repository.DeliveryDashboardRepository
import java.math.BigDecimal

class DeliveryDashboardRepositoryImpl(
    private val source: DeliveryDashboardDataSource
) : DeliveryDashboardRepository {
    override suspend fun getDashboard(): AppResult<DeliveryDashboard> = when (val result = source.getDashboard()) {
        is AppResult.Failure -> result
        is AppResult.Success -> map(result.data)
    }

    private fun map(dto: DeliveryDashboardResponseDto): AppResult<DeliveryDashboard> {
        val recent = dto.recentOrders?.map { item ->
            val id = item.orderId?.trim()
            val status = item.status?.trim()
            if (id.isNullOrEmpty() || status.isNullOrEmpty()) {
                return AppResult.Failure(AppFailure("Invalid delivery dashboard response.", type = FailureType.SERVER))
            }
            DeliveryOrderSummary(
                orderId = id,
                customerName = item.customerName?.trim().orEmpty(),
                customerPhone = item.customerPhone?.trim()?.ifEmpty { null },
                address = item.address?.trim()?.ifEmpty { null },
                totalAmount = (item.totalAmount ?: item.amount)?.trim()?.takeIf { it.isNotEmpty() }?.let { value ->
                    value.toBigDecimalOrNull()
                },
                currencyCode = item.currencyCode?.trim()?.ifEmpty { null }
                    ?: dto.currencyCode?.trim()?.ifEmpty { null },
                status = status,
                paymentStatus = item.paymentStatus?.trim()?.ifEmpty { null },
                assignedAt = item.assignedAt?.trim()?.ifEmpty { null },
                createdAt = item.createdAt?.trim()?.ifEmpty { null }
            )
        }.orEmpty()

        return AppResult.Success(
            DeliveryDashboard(
                activeOrders = dto.activeOrders,
                assignedOrders = dto.assignedOrders,
                pickedUpOrders = dto.pickedUpOrders,
                outForDeliveryOrders = dto.outForDeliveryOrders,
                completedToday = dto.completedToday,
                todayEarnings = dto.todayEarnings?.trim()?.takeIf { it.isNotEmpty() }?.let { it.toBigDecimalOrNull() },
                currencyCode = dto.currencyCode?.trim()?.ifEmpty { null },
                availability = dto.availability?.trim()?.ifEmpty { null },
                recentOrders = recent,
                recentOrdersAvailable = dto.recentOrders != null
            )
        )
    }
}
