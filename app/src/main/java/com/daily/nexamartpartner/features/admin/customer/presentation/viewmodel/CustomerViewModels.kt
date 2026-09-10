package com.daily.nexamartpartner.features.admin.customer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.admin.customer.domain.model.*
import com.daily.nexamartpartner.features.admin.customer.domain.usecase.*
import com.daily.nexamartpartner.features.admin.customer.presentation.state.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface CustomerEvent {
    data object SessionExpired : CustomerEvent
    data class Message(val text: String) : CustomerEvent
    data class ActionSucceeded(val action: CustomerAdminAction) : CustomerEvent
}

class CustomerListViewModel(private val getCustomers: GetCustomersUseCase) : ViewModel() {
    private val _state = MutableStateFlow(CustomerListUiState())
    val uiState: StateFlow<CustomerListUiState> = _state.asStateFlow()
    private val _events = MutableSharedFlow<CustomerEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
    private val items = mutableListOf<Customer>()
    private var job: Job? = null
    private var searchJob: Job? = null
    private var page = 0
    private var hasNext = true

    init { load(0, true) }

    fun search(value: String) {
        _state.update { it.copy(searchQuery = value) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch { delay(350); load(0, true) }
    }

    fun setStatus(value: CustomerAccountStatus?) {
        _state.update { it.copy(status = value?.backendValue) }
        load(0, true)
    }

    fun clear() { _state.update { it.copy(searchQuery = "", status = null) }; load(0, true) }
    fun refresh() { if (job?.isActive == true) return; _state.update { it.copy(isRefreshing = true) }; load(0, false) }
    fun retry() = load(0, true)
    fun next() { if (!hasNext || job?.isActive == true) return; _state.update { it.copy(isLoadingMore = true) }; load(page + 1, false, true) }

    private fun load(target: Int, showLoading: Boolean, append: Boolean = false) {
        if (job?.isActive == true) { if (append) return; job?.cancel() }
        if (showLoading) _state.update { it.copy(content = CustomerListContent.Loading) }
        job = viewModelScope.launch {
            val s = _state.value
            val status = s.status?.let(CustomerAccountStatus::fromRaw)
            when (val result = getCustomers(CustomerQuery(target, 20, s.searchQuery.trim().ifBlank { null }, status))) {
                is AppResult.Success -> {
                    if (!append) items.clear()
                    items.addAll(result.data.customers)
                    page = result.data.page
                    hasNext = result.data.hasNextPage
                    _state.update { it.copy(isRefreshing = false, isLoadingMore = false, content = if (items.isEmpty()) CustomerListContent.Empty(if (s.searchQuery.isNotBlank()) "No customers match your search." else "No customers found.") else CustomerListContent.Success(items.toList(), hasNext)) }
                }
                is AppResult.Failure -> {
                    if (result.error.type == FailureType.UNAUTHORIZED) _events.tryEmit(CustomerEvent.SessionExpired)
                    if (append && items.isNotEmpty()) {
                        _events.tryEmit(CustomerEvent.Message(result.error.message))
                        _state.update { it.copy(isLoadingMore = false, isRefreshing = false) }
                    } else {
                        _state.update { it.copy(isLoadingMore = false, isRefreshing = false, content = if (result.error.type == FailureType.CONTRACT_MISSING) CustomerListContent.Unavailable(result.error.message) else CustomerListContent.Error(result.error.message)) }
                    }
                }
            }
        }
    }
}

class CustomerDetailsViewModel(private val id: String, private val get: GetCustomerDetailsUseCase, private val action: PerformCustomerAdminActionUseCase) : ViewModel() {
    private val _state = MutableStateFlow(CustomerDetailsUiState())
    val uiState = _state.asStateFlow()
    private val _events = MutableSharedFlow<CustomerEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
    private var job: Job? = null
    init { load(true) }
    fun refresh() { if (job?.isActive == true) return; _state.update { it.copy(isRefreshing = true) }; load(false) }
    fun retry() = load(true)
    fun perform(a: CustomerAdminAction) {
        if (_state.value.actionInProgress != null) return
        viewModelScope.launch {
            _state.update { it.copy(actionInProgress = a.backendValue) }
            when (val r = action(id, a)) {
                is AppResult.Success -> { _events.emit(CustomerEvent.ActionSucceeded(a)); load(false) }
                is AppResult.Failure -> { if (r.error.type == FailureType.UNAUTHORIZED) _events.emit(CustomerEvent.SessionExpired); else _events.emit(CustomerEvent.Message(r.error.message)); _state.update { it.copy(actionInProgress = null) } }
            }
        }
    }
    private fun load(show: Boolean) {
        job?.cancel()
        if (show) _state.update { it.copy(content = CustomerDetailsContent.Loading) }
        job = viewModelScope.launch {
            when (val r = get(id)) {
                is AppResult.Success -> _state.update { it.copy(isRefreshing = false, actionInProgress = null, content = CustomerDetailsContent.Success(r.data)) }
                is AppResult.Failure -> { if (r.error.type == FailureType.UNAUTHORIZED) _events.emit(CustomerEvent.SessionExpired); _state.update { it.copy(isRefreshing = false, content = if (r.error.type == FailureType.CONTRACT_MISSING) CustomerDetailsContent.Unavailable(r.error.message) else CustomerDetailsContent.Error(r.error.message)) } }
            }
        }
    }
}
