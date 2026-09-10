package com.daily.nexamartpartner.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.data.repository.defaultAdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderFilters
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSort
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.model.PagedAdminOrders
import com.daily.nexamartpartner.features.admin.domain.model.PaymentStatus
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrdersUseCase
import com.daily.nexamartpartner.features.admin.presentation.state.AdminOrdersUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminOrdersViewModel(
    private val getAdminOrdersUseCase: GetAdminOrdersUseCase
) : ViewModel() {
    sealed interface Event {
        data object SessionExpired : Event
        data class ShowMessage(val message: String) : Event
    }

    private val defaultQuery = defaultAdminOrdersQuery()
    private val loadedOrders = mutableListOf<com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSummary>()

    private val _uiState = MutableStateFlow(
        AdminOrdersUiState(
            searchQuery = "",
            selectedStatusFilter = null,
            selectedPaymentFilterLabel = "All Payments",
            selectedSort = defaultQuery.sort,
            isRefreshing = false,
            isLoadingMore = false,
            content = AdminOrdersUiState.ContentState.Loading,
            filters = defaultQuery.filters
        )
    )
    val uiState: StateFlow<AdminOrdersUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var searchDebounceJob: Job? = null
    private var fetchJob: Job? = null
    private var currentPage = 0
    private var hasNextPage = true

    init {
        loadFirstPage(forceLoadingState = true)
    }

    fun onSearchQueryChanged(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(350)
            loadFirstPage(forceLoadingState = true)
        }
    }

    fun onStatusFilterSelected(status: OrderStatus?) {
        _uiState.update {
            it.copy(
                selectedStatusFilter = status,
                filters = it.filters.copy(status = status)
            )
        }
        loadFirstPage(forceLoadingState = true)
    }

    fun onPaymentFilterSelected(status: PaymentStatus?) {
        _uiState.update {
            it.copy(
                selectedPaymentFilterLabel = status?.backendValue ?: "All Payments",
                filters = it.filters.copy(paymentStatus = status)
            )
        }
        loadFirstPage(forceLoadingState = true)
    }

    fun onSortSelected(sort: AdminOrderSort) {
        _uiState.update { it.copy(selectedSort = sort) }
        loadFirstPage(forceLoadingState = true)
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedStatusFilter = null,
                selectedPaymentFilterLabel = "All Payments",
                selectedSort = defaultQuery.sort,
                filters = AdminOrderFilters()
            )
        }
        loadFirstPage(forceLoadingState = true)
    }

    fun refresh() {
        if (fetchJob?.isActive == true) return
        _uiState.update { it.copy(isRefreshing = true) }
        loadFirstPage(forceLoadingState = false)
    }

    fun retry() {
        loadFirstPage(forceLoadingState = true)
    }

    fun loadNextPage() {
        if (!hasNextPage || fetchJob?.isActive == true) return
        _uiState.update { it.copy(isLoadingMore = true) }
        loadPage(currentPage + 1, append = true, forceLoadingState = false)
    }

    private fun loadFirstPage(forceLoadingState: Boolean) {
        loadPage(page = 0, append = false, forceLoadingState = forceLoadingState)
    }

    private fun loadPage(page: Int, append: Boolean, forceLoadingState: Boolean) {
        if (fetchJob?.isActive == true) return
        if (forceLoadingState) {
            _uiState.update { it.copy(content = AdminOrdersUiState.ContentState.Loading) }
        }
        fetchJob = viewModelScope.launch {
            val state = _uiState.value
            val query = AdminOrdersQuery(
                page = page,
                pageSize = defaultQuery.pageSize,
                searchText = state.searchQuery.trim().ifBlank { null },
                filters = state.filters,
                sort = state.selectedSort
            )
            when (val result = getAdminOrdersUseCase(query)) {
                is AppResult.Success -> onPageSuccess(result.data, append)
                is AppResult.Failure -> onPageFailure(result.error.type, result.error.message)
            }
        }
    }

    private fun onPageSuccess(page: PagedAdminOrders, append: Boolean) {
        if (!append) loadedOrders.clear()
        loadedOrders.addAll(page.orders)
        currentPage = page.page
        hasNextPage = page.hasNextPage

        val content = if (loadedOrders.isEmpty()) {
            val query = _uiState.value.searchQuery
            val hasFilters = _uiState.value.filters.status != null || _uiState.value.filters.paymentStatus != null
            AdminOrdersUiState.ContentState.Empty(
                title = "No orders found",
                message = when {
                    query.isNotBlank() -> "No orders match your search."
                    hasFilters -> "No orders match the selected filters."
                    else -> "No orders found."
                },
                showClearFilters = query.isNotBlank() || hasFilters
            )
        } else {
            AdminOrdersUiState.ContentState.Success(
                orders = loadedOrders.toList(),
                hasNextPage = hasNextPage
            )
        }

        _uiState.update {
            it.copy(
                isRefreshing = false,
                isLoadingMore = false,
                content = content
            )
        }
    }

    private fun onPageFailure(type: FailureType, message: String) {
        if (type == FailureType.UNAUTHORIZED) {
            _events.tryEmit(Event.SessionExpired)
        }
        val content = when (type) {
            FailureType.CONTRACT_MISSING -> AdminOrdersUiState.ContentState.Unavailable(
                title = "Orders data unavailable",
                message = message
            )

            else -> AdminOrdersUiState.ContentState.Error(
                title = "Unable to load orders",
                message = message.ifBlank { "Please try again." }
            )
        }
        _uiState.update {
            it.copy(
                isRefreshing = false,
                isLoadingMore = false,
                content = content
            )
        }
    }
}
