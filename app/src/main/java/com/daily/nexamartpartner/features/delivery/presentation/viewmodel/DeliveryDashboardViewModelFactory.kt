package com.daily.nexamartpartner.features.delivery.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.auth.domain.session.SessionManager
import com.daily.nexamartpartner.features.delivery.domain.usecase.GetDeliveryDashboardUseCase

class DeliveryDashboardViewModelFactory(
    private val useCase: GetDeliveryDashboardUseCase,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(DeliveryDashboardViewModel::class.java))
        return DeliveryDashboardViewModel(useCase, sessionManager) as T
    }
}
