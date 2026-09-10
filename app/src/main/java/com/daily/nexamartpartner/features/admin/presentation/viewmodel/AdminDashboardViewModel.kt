package com.daily.nexamartpartner.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.domain.model.AdminDashboard
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminDashboardUseCase
import com.daily.nexamartpartner.features.admin.presentation.state.AdminDashboardUiState
import com.daily.nexamartpartner.features.auth.domain.session.SessionManager
import java.util.Calendar
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminDashboardViewModel(
    private val getAdminDashboardUseCase: GetAdminDashboardUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    sealed interface Event {
        data object SessionExpired : Event
    }

    private val _uiState = MutableStateFlow(
        AdminDashboardUiState(
            adminName = resolveAdminName(),
            greeting = resolveGreeting(),
            isRefreshing = false,
            content = AdminDashboardUiState.ContentState.Loading
        )
    )
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var loadJob: Job? = null

    init {
        loadDashboard(isRefresh = false)
    }

    fun refresh() {
        loadDashboard(isRefresh = true)
    }

    fun retry() {
        loadDashboard(isRefresh = false)
    }

    private fun loadDashboard(isRefresh: Boolean) {
        if (loadJob?.isActive == true) return

        if (isRefresh) {
            _uiState.update { it.copy(isRefreshing = true, greeting = resolveGreeting()) }
        } else {
            _uiState.update {
                it.copy(
                    adminName = resolveAdminName(),
                    greeting = resolveGreeting(),
                    content = AdminDashboardUiState.ContentState.Loading
                )
            }
        }

        loadJob = viewModelScope.launch {
            when (val result = getAdminDashboardUseCase()) {
                is AppResult.Success -> onDashboardSuccess(result.data, isRefresh)
                is AppResult.Failure -> onDashboardFailure(
                    failureType = result.error.type,
                    message = result.error.message,
                    isRefresh = isRefresh
                )
            }
        }
    }

    private fun onDashboardSuccess(dashboard: AdminDashboard, isRefresh: Boolean) {
        val contentState = when {
            dashboard.hasData -> AdminDashboardUiState.ContentState.Success(dashboard)
            dashboard.recentOrdersAvailable -> AdminDashboardUiState.ContentState.Empty(
                title = "No dashboard activity yet",
                message = "No orders or sales activity is available right now."
            )
            else -> AdminDashboardUiState.ContentState.Unavailable(
                title = "Dashboard data unavailable",
                message = "Dashboard API response is missing required fields."
            )
        }
        _uiState.update {
            it.copy(
                greeting = resolveGreeting(),
                isRefreshing = false,
                content = contentState
            )
        }
    }

    private fun onDashboardFailure(failureType: FailureType, message: String, isRefresh: Boolean) {
        if (failureType == FailureType.UNAUTHORIZED) {
            _events.tryEmit(Event.SessionExpired)
        }
        val contentState = when (failureType) {
            FailureType.CONTRACT_MISSING -> AdminDashboardUiState.ContentState.Unavailable(
                title = "Dashboard data unavailable",
                message = message
            )

            else -> AdminDashboardUiState.ContentState.Error(
                title = "Unable to load dashboard",
                message = "Please try again."
            )
        }
        _uiState.update {
            it.copy(
                greeting = resolveGreeting(),
                isRefreshing = false,
                content = contentState
            )
        }
    }

    private fun resolveAdminName(): String? {
        val current = sessionManager.currentSession.value?.name?.trim().orEmpty()
        return current.ifEmpty { null }
    }

    private fun resolveGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}
