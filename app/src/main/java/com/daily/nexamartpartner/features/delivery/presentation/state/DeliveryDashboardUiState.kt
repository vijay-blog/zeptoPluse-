package com.daily.nexamartpartner.features.delivery.presentation.state

import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryDashboard

data class DeliveryDashboardUiState(
    val partnerName: String?,
    val isRefreshing: Boolean,
    val content: ContentState
) {
    sealed interface ContentState {
        data object Loading : ContentState
        data class Success(val dashboard: DeliveryDashboard) : ContentState
        data class Empty(val title: String, val message: String) : ContentState
        data class Error(val title: String, val message: String) : ContentState
        data class Unavailable(val title: String, val message: String) : ContentState
    }
}
