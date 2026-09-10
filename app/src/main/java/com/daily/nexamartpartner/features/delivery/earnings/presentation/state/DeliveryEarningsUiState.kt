package com.daily.nexamartpartner.features.delivery.earnings.presentation.state

import com.daily.nexamartpartner.features.delivery.earnings.domain.model.*

data class DeliveryEarningsUiState(
    val filters: Filters = Filters(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val summary: SummaryState = SummaryState.Loading,
    val history: HistoryState = HistoryState.Loading
) {
    data class Filters(val fromDate: String? = null, val toDate: String? = null)
    sealed interface SummaryState { data object Loading : SummaryState; data class Success(val value: DeliveryEarningsSummary) : SummaryState; data class Empty(val message: String) : SummaryState; data class Error(val title: String, val message: String) : SummaryState; data class Unavailable(val title: String, val message: String) : SummaryState }
    sealed interface HistoryState { data object Loading : HistoryState; data class Success(val entries: List<DeliveryEarningEntry>, val hasNext: Boolean) : HistoryState; data class Empty(val title: String, val message: String) : HistoryState; data class Error(val title: String, val message: String) : HistoryState; data class Unavailable(val title: String, val message: String) : HistoryState }
}
