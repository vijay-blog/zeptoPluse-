package com.daily.nexamartpartner.features.admin.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.admin.domain.model.AdminDashboard
import com.daily.nexamartpartner.features.admin.domain.repository.AdminDashboardRepository

class GetAdminDashboardUseCase(
    private val repository: AdminDashboardRepository
) {
    suspend operator fun invoke(): AppResult<AdminDashboard> = repository.getDashboard()
}
