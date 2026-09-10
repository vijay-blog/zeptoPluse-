package com.daily.nexamartpartner.features.delivery.earnings.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.delivery.earnings.data.contract.DeliveryEarningsContract
import com.daily.nexamartpartner.features.delivery.earnings.data.model.*
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.DeliveryEarningsQuery

interface DeliveryEarningsDataSource {
    suspend fun getSummary(query: DeliveryEarningsQuery): AppResult<DeliveryEarningsSummaryDto>
    suspend fun getHistory(query: DeliveryEarningsQuery): AppResult<DeliveryEarningsPageDto>
}

class DeliveryEarningsDataSourceImpl(
    private val api: DeliveryEarningsApi,
    private val contract: DeliveryEarningsContract,
    private val executor: ApiCallExecutor
) : DeliveryEarningsDataSource {
    override suspend fun getSummary(query: DeliveryEarningsQuery): AppResult<DeliveryEarningsSummaryDto> {
        val path = contract.summaryPath ?: return AppResult.Failure(AppFailure("Delivery earnings summary API contract is not configured.", type = FailureType.CONTRACT_MISSING))
        val params = contract.buildSummaryQuery(query) ?: return AppResult.Failure(AppFailure("Delivery earnings summary query contract is not configured.", type = FailureType.CONTRACT_MISSING))
        return executor.execute { api.getSummary(path, params) }
    }
    override suspend fun getHistory(query: DeliveryEarningsQuery): AppResult<DeliveryEarningsPageDto> {
        val path = contract.historyPath ?: return AppResult.Failure(AppFailure("Delivery earnings history API contract is not configured.", type = FailureType.CONTRACT_MISSING))
        val params = contract.buildHistoryQuery(query) ?: return AppResult.Failure(AppFailure("Delivery earnings history query contract is not configured.", type = FailureType.CONTRACT_MISSING))
        return executor.execute { api.getHistory(path, params) }
    }
}
