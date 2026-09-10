package com.daily.nexamartpartner.features.admin.data.source

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.data.model.AdminDashboardResponseDto

interface AdminDashboardRemoteDataSource {
    suspend fun getDashboard(): AppResult<AdminDashboardResponseDto>
}
