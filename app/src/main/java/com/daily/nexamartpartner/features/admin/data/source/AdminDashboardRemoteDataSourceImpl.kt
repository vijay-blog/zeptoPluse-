package com.daily.nexamartpartner.features.admin.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.data.contract.AdminDashboardContract
import com.daily.nexamartpartner.features.admin.data.model.AdminDashboardResponseDto

class AdminDashboardRemoteDataSourceImpl(
    private val api: AdminDashboardApi,
    private val contract: AdminDashboardContract,
    private val apiCallExecutor: ApiCallExecutor
) : AdminDashboardRemoteDataSource {
    override suspend fun getDashboard(): AppResult<AdminDashboardResponseDto> {
        val endpointPath = contract.dashboardEndpointPath
            ?: return AppResult.Failure(
                AppFailure(
                    message = "Dashboard data unavailable. Backend contract is not confirmed yet.",
                    type = FailureType.CONTRACT_MISSING
                )
            )
        return apiCallExecutor.execute { api.getDashboard(endpointPath) }
    }
}
