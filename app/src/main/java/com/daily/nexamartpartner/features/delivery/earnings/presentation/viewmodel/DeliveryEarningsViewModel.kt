package com.daily.nexamartpartner.features.delivery.earnings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.delivery.earnings.domain.model.*
import com.daily.nexamartpartner.features.delivery.earnings.domain.usecase.*
import com.daily.nexamartpartner.features.delivery.earnings.presentation.state.DeliveryEarningsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeliveryEarningsViewModel(private val summaryUseCase: GetDeliveryEarningsSummaryUseCase, private val historyUseCase: GetDeliveryEarningsHistoryUseCase) : ViewModel() {
    sealed interface Event { data object SessionExpired : Event }
    private val _state = MutableStateFlow(DeliveryEarningsUiState())
    val state: StateFlow<DeliveryEarningsUiState> = _state.asStateFlow()
    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()
    private var job: Job? = null
    private var loaded = mutableListOf<DeliveryEarningEntry>()
    private var page = 0
    private var next = true
    init { load(true) }
    fun setDateRange(from: String?, to: String?) { _state.update { it.copy(filters = it.filters.copy(fromDate = from, toDate = to)) }; load(true) }
    fun refresh() { if (job?.isActive == true) return; _state.update { it.copy(isRefreshing = true) }; load(false) }
    fun retry() = load(true)
    fun nextPage() { if (!next || job?.isActive == true) return; _state.update { it.copy(isLoadingMore = true) }; fetch(page + 1, true) }
    private fun load(reset: Boolean) { fetch(0, false, reset) }
    private fun fetch(targetPage: Int, append: Boolean, reset: Boolean = false) {
        if (job?.isActive == true) return
        val f = _state.value.filters
        if (reset) _state.update { it.copy(isLoading = true, summary = DeliveryEarningsUiState.SummaryState.Loading, history = DeliveryEarningsUiState.HistoryState.Loading) }
        job = viewModelScope.launch {
            val query = DeliveryEarningsQuery(targetPage, 20, f.fromDate, f.toDate)
            val summary = summaryUseCase(query)
            val history = historyUseCase(query)
            var unauthorized = false
            when (summary) {
                is AppResult.Success -> _state.update { it.copy(summary = DeliveryEarningsUiState.SummaryState.Success(summary.data)) }
                is AppResult.Failure -> { unauthorized = summary.error.type == FailureType.UNAUTHORIZED; _state.update { it.copy(summary = failureSummary(summary.error.type, summary.error.message)) } }
            }
            when (history) {
                is AppResult.Success -> {
                    if (!append) loaded.clear(); loaded.addAll(history.data.entries); page = history.data.page; next = history.data.hasNextPage
                    _state.update { it.copy(isLoading = false, isRefreshing = false, isLoadingMore = false, history = if (loaded.isEmpty()) DeliveryEarningsUiState.HistoryState.Empty("No earnings yet", "Completed delivery earnings will appear here.") else DeliveryEarningsUiState.HistoryState.Success(loaded.toList(), next)) }
                }
                is AppResult.Failure -> { unauthorized = unauthorized || history.error.type == FailureType.UNAUTHORIZED; _state.update { it.copy(isLoading = false, isRefreshing = false, isLoadingMore = false, history = failureHistory(history.error.type, history.error.message)) } }
            }
            if (unauthorized) _events.tryEmit(Event.SessionExpired)
        }
    }
    private fun failureSummary(type: FailureType, message: String): DeliveryEarningsUiState.SummaryState = if (type == FailureType.CONTRACT_MISSING) DeliveryEarningsUiState.SummaryState.Unavailable("Earnings unavailable", message) else if (type == FailureType.UNAUTHORIZED) DeliveryEarningsUiState.SummaryState.Error("Session expired", "Please sign in again.") else DeliveryEarningsUiState.SummaryState.Error("Unable to load earnings", message)
    private fun failureHistory(type: FailureType, message: String): DeliveryEarningsUiState.HistoryState = if (type == FailureType.CONTRACT_MISSING) DeliveryEarningsUiState.HistoryState.Unavailable("Earnings history unavailable", message) else if (type == FailureType.UNAUTHORIZED) DeliveryEarningsUiState.HistoryState.Error("Session expired", "Please sign in again.") else DeliveryEarningsUiState.HistoryState.Error("Unable to load earnings history", message)
}
