package com.daily.nexamartpartner.features.delivery.availability.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.availability.domain.model.DeliveryAvailabilityUpdate
import com.daily.nexamartpartner.features.delivery.availability.domain.usecase.*
import com.daily.nexamartpartner.features.delivery.availability.presentation.state.DeliveryAvailabilityUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeliveryAvailabilityViewModel(
    private val get: GetDeliveryAvailabilityUseCase,
    private val update: UpdateDeliveryAvailabilityUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(DeliveryAvailabilityUiState())
    val state: StateFlow<DeliveryAvailabilityUiState> = _state.asStateFlow()
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    sealed interface Event { data object SessionExpired : Event; data class Message(val text: String) : Event }

    init { load(false) }
    fun load(refresh: Boolean = false) { viewModelScope.launch { _state.update { it.copy(loading = !refresh, refreshing = refresh, error = null, unavailable = false) }; handle(get()) { data -> _state.update { it.copy(loading = false, refreshing = false, availability = data, error = null, unavailable = false) } } } }
    fun refresh() { load(true) }
    fun setAvailable(value: Boolean) {
        val current = _state.value.availability ?: return
        if (!current.canChange || _state.value.saving || current.available == value) return
        viewModelScope.launch {
            _state.update { it.copy(saving = true, error = null) }
            handle(update(DeliveryAvailabilityUpdate(value))) { data ->
                _state.update { it.copy(saving = false, availability = data) }
                _events.emit(Event.Message(if (value) "You are now available for deliveries." else "You are now offline."))
            }
            if (_state.value.saving) _state.update { it.copy(saving = false) }
        }
    }
    private suspend fun <T> handle(result: AppResult<T>, success: suspend (T) -> Unit) {
        when (result) {
            is AppResult.Success -> success(result.data)
            is AppResult.Failure -> when (result.error.type) {
                FailureType.UNAUTHORIZED -> _events.emit(Event.SessionExpired)
                FailureType.CONTRACT_MISSING -> _state.update { it.copy(loading = false, refreshing = false, saving = false, unavailable = true, error = null) }
                else -> _state.update { it.copy(loading = false, refreshing = false, saving = false, error = result.error.message, unavailable = false) }
            }
        }
    }
}
