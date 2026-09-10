package com.daily.nexamartpartner.features.delivery.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrderSummary
import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrdersQuery
import com.daily.nexamartpartner.features.delivery.domain.usecase.GetDeliveryHistoryUseCase
import com.daily.nexamartpartner.features.delivery.presentation.state.DeliveryHistoryUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeliveryHistoryViewModel(private val getHistory: GetDeliveryHistoryUseCase) : ViewModel() {
    sealed interface Event { data object SessionExpired: Event }
    private val _state=MutableStateFlow(DeliveryHistoryUiState())
    val state:StateFlow<DeliveryHistoryUiState> = _state.asStateFlow()
    private val _events=MutableSharedFlow<Event>(extraBufferCapacity=1)
    val events:SharedFlow<Event> = _events.asSharedFlow()
    private val loaded=mutableListOf<DeliveryOrderSummary>(); private var page=0; private var next=true; private var job:Job?=null; private var debounce:Job?=null
    init { load(true) }
    fun search(v:String){_state.update{it.copy(filters=it.filters.copy(search=v))}; debounce?.cancel(); debounce=viewModelScope.launch{delay(350);load(true)}}
    fun setStatus(v:String?){_state.update{it.copy(filters=it.filters.copy(status=v))};load(true)}
    fun setDateRange(from:String?,to:String?){_state.update{it.copy(filters=it.filters.copy(fromDate=from,toDate=to))};load(true)}
    fun refresh(){if(job?.isActive==true)return;_state.update{it.copy(isRefreshing=true)};load(false)}
    fun retry(){load(true)}
    fun nextPage(){if(!next||job?.isActive==true)return;_state.update{it.copy(isLoadingMore=true)};fetch(page+1,true)}
    private fun load(force:Boolean){fetch(0,false,force)}
    private fun fetch(p:Int,append:Boolean,force:Boolean=false){if(job?.isActive==true)return;if(force)_state.update{it.copy(content=DeliveryHistoryUiState.Content.Loading)};val f=_state.value.filters;job=viewModelScope.launch{when(val r=getHistory(DeliveryOrdersQuery(p,20,f.search.trim().ifBlank{null},f.status,f.fromDate,f.toDate))){is AppResult.Success->{if(!append)loaded.clear();loaded.addAll(r.data.orders);page=r.data.page;next=r.data.hasNextPage;_state.update{it.copy(isRefreshing=false,isLoadingMore=false,content=if(loaded.isEmpty())DeliveryHistoryUiState.Content.Empty("No delivery history","Completed or cancelled deliveries will appear here.") else DeliveryHistoryUiState.Content.Success(loaded.toList(),next))}};is AppResult.Failure->{_state.update{it.copy(isRefreshing=false,isLoadingMore=false,content=if(r.error.type==FailureType.CONTRACT_MISSING)DeliveryHistoryUiState.Content.Unavailable("History unavailable",r.error.message) else if(r.error.type==FailureType.UNAUTHORIZED)DeliveryHistoryUiState.Content.Error("Session expired","Please sign in again.") else DeliveryHistoryUiState.Content.Error("Unable to load history",r.error.message))};if(r.error.type==FailureType.UNAUTHORIZED)_events.tryEmit(Event.SessionExpired)}}}}
}
