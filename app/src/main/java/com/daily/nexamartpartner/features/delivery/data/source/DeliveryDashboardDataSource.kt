package com.daily.nexamartpartner.features.delivery.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.delivery.data.contract.DeliveryDashboardContract
import com.daily.nexamartpartner.features.delivery.data.model.DeliveryDashboardResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface DeliveryDashboardApi {
    @GET
    suspend fun getDashboard(@Url path: String): Response<DeliveryDashboardResponseDto>
}

interface DeliveryDashboardDataSource {
    suspend fun getDashboard(): AppResult<DeliveryDashboardResponseDto>
}

class DeliveryDashboardDataSourceImpl(
    private val api: DeliveryDashboardApi,
    private val contract: DeliveryDashboardContract,
    private val executor: ApiCallExecutor
) : DeliveryDashboardDataSource {
    override suspend fun getDashboard(): AppResult<DeliveryDashboardResponseDto> {
        val path = contract.dashboardPath
            ?: return AppResult.Failure(
                AppFailure(
                    "Delivery dashboard API is not confirmed by the backend.",
                    type = FailureType.CONTRACT_MISSING
                )
            )
        return executor.execute { api.getDashboard(path) }
    }
}
