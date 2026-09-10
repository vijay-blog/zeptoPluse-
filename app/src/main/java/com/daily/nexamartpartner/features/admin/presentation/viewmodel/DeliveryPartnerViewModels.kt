package com.daily.nexamartpartner.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerFilters
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerSummary
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnersQuery
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAccountStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAvailability
import com.daily.nexamartpartner.features.admin.domain.model.PartnerVerificationStatus
import com.daily.nexamartpartner.features.admin.domain.usecase.GetDeliveryPartnerDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetDeliveryPartnersUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateDeliveryPartnerUseCase
import com.daily.nexamartpartner.features.admin.presentation.state.DeliveryPartnerDetailsUiState
import com.daily.nexamartpartner.features.admin.presentation.state.DeliveryPartnerListUiState
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

sealed interface DeliveryPartnerEvent {
    data object SessionExpired : DeliveryPartnerEvent
    data class Message(val text: String) : DeliveryPartnerEvent
}

class DeliveryPartnerListViewModel(
    private val getPartners: GetDeliveryPartnersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeliveryPartnerListUiState())
    val uiState: StateFlow<DeliveryPartnerListUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<DeliveryPartnerEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DeliveryPartnerEvent> = _events.asSharedFlow()
    private val partners = mutableListOf<DeliveryPartnerSummary>()
    private var requestJob: Job? = null
    private var searchJob: Job? = null
    private var page = 0
    private var hasNext = true

    init { load(0, append = false, showLoading = true) }

    fun search(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            load(0, append = false, showLoading = true)
        }
    }

    fun applyFilters(filters: DeliveryPartnerFilters) {
        _uiState.update { it.copy(filters = filters) }
        load(0, append = false, showLoading = true)
    }

    fun clearFilters() {
        _uiState.update { it.copy(searchQuery = "", filters = DeliveryPartnerFilters()) }
        load(0, append = false, showLoading = true)
    }

    fun refresh() {
        if (requestJob?.isActive == true) return
        _uiState.update { it.copy(isRefreshing = true) }
        load(0, append = false, showLoading = false)
    }

    fun retry() = load(0, append = false, showLoading = true)

    fun loadNext() {
        if (!hasNext || requestJob?.isActive == true) return
        _uiState.update { it.copy(isLoadingMore = true) }
        load(page + 1, append = true, showLoading = false)
    }

    private fun load(targetPage: Int, append: Boolean, showLoading: Boolean) {
        if (requestJob?.isActive == true) return
        if (showLoading) _uiState.update { it.copy(content = DeliveryPartnerListUiState.Content.Loading) }
        requestJob = viewModelScope.launch {
            val state = _uiState.value
            val query = DeliveryPartnersQuery(
                targetPage, 20, state.searchQuery.trim().ifBlank { null }, state.filters
            )
            when (val result = getPartners(query)) {
                is AppResult.Success -> {
                    if (!append) partners.clear()
                    partners.addAll(result.data.partners)
                    page = result.data.page
                    hasNext = result.data.hasNextPage
                    val hasCriteria = state.searchQuery.isNotBlank() || state.filters != DeliveryPartnerFilters()
                    val content = if (partners.isEmpty()) {
                        DeliveryPartnerListUiState.Content.Empty(
                            when {
                                state.searchQuery.isNotBlank() -> "No delivery partners match your search."
                                hasCriteria -> "No partners match your filters."
                                else -> "No delivery partners found."
                            },
                            hasCriteria
                        )
                    } else DeliveryPartnerListUiState.Content.Success(partners.toList(), hasNext)
                    _uiState.update {
                        it.copy(isRefreshing = false, isLoadingMore = false, content = content)
                    }
                }
                is AppResult.Failure -> fail(result.error.type, result.error.message)
            }
        }
    }

    private fun fail(type: FailureType, message: String) {
        if (type == FailureType.UNAUTHORIZED) _events.tryEmit(DeliveryPartnerEvent.SessionExpired)
        val content = if (type == FailureType.CONTRACT_MISSING) {
            DeliveryPartnerListUiState.Content.Unavailable(message)
        } else DeliveryPartnerListUiState.Content.Error("Unable to load delivery partners. Please try again.")
        _uiState.update { it.copy(isRefreshing = false, isLoadingMore = false, content = content) }
    }
}

class DeliveryPartnerDetailsViewModel(
    private val partnerId: String,
    private val getDetails: GetDeliveryPartnerDetailsUseCase,
    private val updatePartner: UpdateDeliveryPartnerUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DeliveryPartnerDetailsUiState())
    val uiState: StateFlow<DeliveryPartnerDetailsUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<DeliveryPartnerEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<DeliveryPartnerEvent> = _events.asSharedFlow()
    private var requestJob: Job? = null

    init { load(true) }
    fun retry() = load(true)
    fun refresh() {
        if (requestJob?.isActive == true) return
        _uiState.update { it.copy(isRefreshing = true) }
        load(false)
    }

    fun performAction(action: PartnerAdminAction, reason: String? = null) {
        if (_uiState.value.actionInProgress != null) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionInProgress = action) }
            when (val result = updatePartner(partnerId, action, reason)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(actionInProgress = null) }
                    _events.tryEmit(DeliveryPartnerEvent.Message("Delivery partner updated."))
                    load(false)
                }
                is AppResult.Failure -> {
                    _uiState.update { it.copy(actionInProgress = null) }
                    if (result.error.type == FailureType.UNAUTHORIZED) {
                        _events.tryEmit(DeliveryPartnerEvent.SessionExpired)
                    } else {
                        _events.tryEmit(DeliveryPartnerEvent.Message(result.error.message))
                    }
                    if (result.error.code == 409) load(false)
                }
            }
        }
    }

    private fun load(showLoading: Boolean) {
        if (requestJob?.isActive == true) return
        if (showLoading) _uiState.update { it.copy(content = DeliveryPartnerDetailsUiState.Content.Loading) }
        requestJob = viewModelScope.launch {
            when (val result = getDetails(partnerId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(isRefreshing = false, content = DeliveryPartnerDetailsUiState.Content.Success(result.data))
                }
                is AppResult.Failure -> {
                    if (result.error.type == FailureType.UNAUTHORIZED) {
                        _events.tryEmit(DeliveryPartnerEvent.SessionExpired)
                    }
                    val content = if (result.error.type == FailureType.CONTRACT_MISSING) {
                        DeliveryPartnerDetailsUiState.Content.Unavailable(result.error.message)
                    } else DeliveryPartnerDetailsUiState.Content.Error("Unable to load delivery partner. Please try again.")
                    _uiState.update { it.copy(isRefreshing = false, content = content) }
                }
            }
        }
    }

}
