package com.daily.nexamartpartner.features.admin.presentation.state

import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderFilters
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSort
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSummary
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus

data class AdminOrdersUiState(
    val searchQuery: String,
    val selectedStatusFilter: OrderStatus?,
    val selectedPaymentFilterLabel: String,
    val selectedSort: AdminOrderSort,
    val isRefreshing: Boolean,
    val isLoadingMore: Boolean,
    val content: ContentState,
    val filters: AdminOrderFilters
) {
    sealed interface ContentState {
        data object Loading : ContentState
        data class Success(
            val orders: List<AdminOrderSummary>,
            val hasNextPage: Boolean
        ) : ContentState
        data class Empty(val title: String, val message: String, val showClearFilters: Boolean) : ContentState
        data class Error(val title: String, val message: String) : ContentState
        data class Unavailable(val title: String, val message: String) : ContentState
    }
}
