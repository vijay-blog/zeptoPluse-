package com.daily.nexamartpartner.features.delivery.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.auth.domain.session.SessionManager
import com.daily.nexamartpartner.features.delivery.domain.usecase.GetDeliveryDashboardUseCase
import com.daily.nexamartpartner.features.delivery.presentation.state.DeliveryDashboardUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeliveryDashboardViewModel(
    private val getDashboard: GetDeliveryDashboardUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    sealed interface Event { data object SessionExpired : Event }

    private val _uiState = MutableStateFlow(
        DeliveryDashboardUiState(
            partnerName = sessionManager.currentSession.value?.name?.trim()?.ifEmpty { null },
            isRefreshing = false,
            content = DeliveryDashboardUiState.ContentState.Loading
        )
    )
    val uiState = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
    private var loadJob: Job? = null

    init { load(false) }
    fun refresh() = load(true)
    fun retry() = load(false)

    private fun load(refresh: Boolean) {
        if (loadJob?.isActive == true) return
        _uiState.update { it.copy(isRefreshing = refresh, partnerName = sessionManager.currentSession.value?.name?.trim()?.ifEmpty { null }, content = if (refresh) it.content else DeliveryDashboardUiState.ContentState.Loading) }
        loadJob = viewModelScope.launch {
            when (val result = getDashboard()) {
                is AppResult.Success -> {
                    val dashboard = result.data
                    val content = when {
                        dashboard.hasData -> DeliveryDashboardUiState.ContentState.Success(dashboard)
                        dashboard.recentOrdersAvailable -> DeliveryDashboardUiState.ContentState.Empty("No delivery activity yet", "No assigned or completed deliveries are available right now.")
                        else -> DeliveryDashboardUiState.ContentState.Unavailable("Dashboard data unavailable", "The delivery dashboard contract is not available yet.")
                    }
                    _uiState.update { it.copy(isRefreshing = false, content = content) }
                }
                is AppResult.Failure -> {
                    if (result.error.type == FailureType.UNAUTHORIZED) _events.tryEmit(Event.SessionExpired)
                    val content = if (result.error.type == FailureType.CONTRACT_MISSING) {
                        DeliveryDashboardUiState.ContentState.Unavailable("Dashboard data unavailable", result.error.message)
                    } else DeliveryDashboardUiState.ContentState.Error("Unable to load dashboard", "Please try again.")
                    _uiState.update { it.copy(isRefreshing = false, content = content) }
                }
            }
        }
    }
}
