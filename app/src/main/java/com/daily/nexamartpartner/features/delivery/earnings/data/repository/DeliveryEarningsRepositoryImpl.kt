package com.daily.nexamartpartner.features.delivery.earnings.data.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.delivery.earnings.data.model.*
import com.daily.nexamartpartner.features.delivery.earnings.data.source.DeliveryEarningsDataSource
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.*
import com.daily.nexamartpartner.features.delivery.earnings.domain.repository.DeliveryEarningsRepository
import java.math.BigDecimal

class DeliveryEarningsRepositoryImpl(private val source: DeliveryEarningsDataSource) : DeliveryEarningsRepository {
    override suspend fun getSummary(query: DeliveryEarningsQuery): AppResult<DeliveryEarningsSummary> = when (val r = source.getSummary(query)) {
        is AppResult.Failure -> r
        is AppResult.Success -> AppResult.Success(r.data.toDomain())
    }
    override suspend fun getHistory(query: DeliveryEarningsQuery): AppResult<PagedDeliveryEarnings> = when (val r = source.getHistory(query)) {
        is AppResult.Failure -> r
        is AppResult.Success -> AppResult.Success(PagedDeliveryEarnings(
            r.data.content.orEmpty().map { it.toDomain() },
            r.data.number ?: query.page,
            r.data.size ?: query.pageSize,
            r.data.totalPages ?: 0,
            r.data.totalElements ?: 0L,
            r.data.last?.not() ?: false
        ))
    }
    private fun DeliveryEarningsSummaryDto.toDomain() = DeliveryEarningsSummary(currencyCode, today.dec(), thisWeek.dec(), thisMonth.dec(), completedDeliveries, pendingPayout.dec(), totalEarned.dec())
    private fun DeliveryEarningEntryDto.toDomain() = DeliveryEarningEntry(id.orEmpty(), orderId, earnedAt, amount.dec(), currencyCode, status, description)
    private fun String?.dec(): BigDecimal? = this?.trim()?.takeIf { it.isNotEmpty() }?.let { runCatching { BigDecimal(it) }.getOrNull() }
}
