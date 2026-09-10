package com.daily.nexamartpartner.features.admin.presentation.state

import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderDetails

data class AdminOrderDetailsUiState(
    val isRefreshing: Boolean,
    val isStatusUpdating: Boolean,
    val isCancelling: Boolean,
    val content: ContentState
) {
    sealed interface ContentState {
        data object Loading : ContentState
        data class Success(val details: AdminOrderDetails) : ContentState
        data class Error(val title: String, val message: String) : ContentState
        data class Unavailable(val title: String, val message: String) : ContentState
    }
}
