package com.daily.nexamartpartner.features.admin.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.model.AdminDashboard

interface AdminDashboardRepository {
    suspend fun getDashboard(): AppResult<AdminDashboard>
}
