package com.daily.nexamartpartner.features.admin.presentation.state

import com.daily.nexamartpartner.features.admin.domain.model.AdminDashboard

data class AdminDashboardUiState(
    val adminName: String?,
    val greeting: String,
    val isRefreshing: Boolean,
    val content: ContentState
) {
    sealed interface ContentState {
        data object Loading : ContentState
        data class Success(val dashboard: AdminDashboard) : ContentState
        data class Empty(val title: String, val message: String) : ContentState
        data class Error(val title: String, val message: String) : ContentState
        data class Unavailable(val title: String, val message: String) : ContentState
    }
}
