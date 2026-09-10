package com.daily.nexamartpartner.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminDashboardUseCase
import com.daily.nexamartpartner.features.auth.domain.session.SessionManager

class AdminDashboardViewModelFactory(
    private val getAdminDashboardUseCase: GetAdminDashboardUseCase,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AdminDashboardViewModel::class.java))
        return AdminDashboardViewModel(
            getAdminDashboardUseCase = getAdminDashboardUseCase,
            sessionManager = sessionManager
        ) as T
    }
}
