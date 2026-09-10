package com.daily.nexamartpartner.features.admin.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.usecase.CancelAdminOrderUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrderDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateAdminOrderStatusUseCase
import com.daily.nexamartpartner.features.admin.presentation.state.AdminOrderDetailsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminOrderDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getAdminOrderDetailsUseCase: GetAdminOrderDetailsUseCase,
    private val updateAdminOrderStatusUseCase: UpdateAdminOrderStatusUseCase,
    private val cancelAdminOrderUseCase: CancelAdminOrderUseCase
) : ViewModel() {
    sealed interface Event {
        data object SessionExpired : Event
        data class ShowMessage(val message: String) : Event
    }

    private val orderId: String = savedStateHandle.get<String>(ARG_ORDER_ID).orEmpty()
    private var loadJob: Job? = null

    private val _uiState = MutableStateFlow(
        AdminOrderDetailsUiState(
            isRefreshing = false,
            isStatusUpdating = false,
            isCancelling = false,
            content = AdminOrderDetailsUiState.ContentState.Loading
        )
    )
    val uiState: StateFlow<AdminOrderDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    init {
        loadDetails(showLoading = true)
    }

    fun refresh() {
        if (loadJob?.isActive == true) return
        _uiState.update { it.copy(isRefreshing = true) }
        loadDetails(showLoading = false)
    }

    fun retry() {
        loadDetails(showLoading = true)
    }

    fun updateStatus(target: OrderStatus) {
        if (_uiState.value.isStatusUpdating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isStatusUpdating = true) }
            when (val result = updateAdminOrderStatusUseCase(orderId, target)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isStatusUpdating = false) }
                    _events.tryEmit(Event.ShowMessage("Order status updated successfully."))
                    loadDetails(showLoading = false)
                }

                is AppResult.Failure -> {
                    _uiState.update { it.copy(isStatusUpdating = false) }
                    handleFailure(result.error.type, result.error.message)
                }
            }
        }
    }

    fun cancelOrder(reason: String?) {
        if (_uiState.value.isCancelling) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCancelling = true) }
            when (val result = cancelAdminOrderUseCase(orderId, reason)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isCancelling = false) }
                    _events.tryEmit(Event.ShowMessage("Order cancelled successfully."))
                    loadDetails(showLoading = false)
                }

                is AppResult.Failure -> {
                    _uiState.update { it.copy(isCancelling = false) }
                    handleFailure(result.error.type, result.error.message)
                }
            }
        }
    }

    private fun loadDetails(showLoading: Boolean) {
        if (loadJob?.isActive == true) return
        if (showLoading) {
            _uiState.update { it.copy(content = AdminOrderDetailsUiState.ContentState.Loading) }
        }
        loadJob = viewModelScope.launch {
            when (val result = getAdminOrderDetailsUseCase(orderId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            content = AdminOrderDetailsUiState.ContentState.Success(result.data)
                        )
                    }
                }

                is AppResult.Failure -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                    handleFailure(result.error.type, result.error.message)
                }
            }
        }
    }

    private fun handleFailure(type: FailureType, message: String) {
        if (type == FailureType.UNAUTHORIZED) {
            _events.tryEmit(Event.SessionExpired)
        }
        val state = when (type) {
            FailureType.CONTRACT_MISSING -> AdminOrderDetailsUiState.ContentState.Unavailable(
                title = "Order details unavailable",
                message = message
            )

            else -> AdminOrderDetailsUiState.ContentState.Error(
                title = "Unable to load order",
                message = message.ifBlank { "Please try again." }
            )
        }
        _uiState.update { it.copy(content = state) }
    }

    companion object {
        const val ARG_ORDER_ID = "orderId"
    }
}
