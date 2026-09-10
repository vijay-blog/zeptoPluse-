package com.daily.nexamartpartner.features.delivery.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.domain.model.*
import com.daily.nexamartpartner.features.delivery.domain.usecase.GetAssignedDeliveryOrdersUseCase
import com.daily.nexamartpartner.features.delivery.presentation.state.DeliveryOrdersUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeliveryOrdersViewModel(private val getOrders:GetAssignedDeliveryOrdersUseCase):ViewModel(){
 sealed interface Event{data object SessionExpired:Event}
 private val _state=MutableStateFlow(DeliveryOrdersUiState());val state:StateFlow<DeliveryOrdersUiState> =_state.asStateFlow();private val loaded=mutableListOf<DeliveryOrderSummary>();private var page=0;private var next=true;private var job:Job?=null;private var debounce:Job?=null
 init{load(false)}
 fun search(v:String){_state.update{it.copy(search=v)};debounce?.cancel();debounce=viewModelScope.launch{delay(350);load(true)}}
 fun refresh(){if(job?.isActive==true)return;_state.update{it.copy(isRefreshing=true)};load(false)}
 fun retry(){load(true)}
 fun nextPage(){if(!next||job?.isActive==true)return;_state.update{it.copy(isLoadingMore=true)};fetch(page+1,true)}
 private fun load(force:Boolean){fetch(0,false,force)}
 private fun fetch(p:Int,append:Boolean=false,force:Boolean=false){if(job?.isActive==true)return;if(force)_state.update{it.copy(content=DeliveryOrdersUiState.Content.Loading)};job=viewModelScope.launch{when(val r=getOrders(DeliveryOrdersQuery(p,20,_state.value.search.trim().ifBlank{null}))){is AppResult.Success->{if(!append)loaded.clear();loaded.addAll(r.data.orders);page=r.data.page;next=r.data.hasNextPage;_state.update{it.copy(isRefreshing=false,isLoadingMore=false,content=if(loaded.isEmpty())DeliveryOrdersUiState.Content.Empty("No assigned orders","There are no delivery orders assigned to you right now.") else DeliveryOrdersUiState.Content.Success(loaded.toList(),next))}};is AppResult.Failure->{_state.update{it.copy(isRefreshing=false,isLoadingMore=false,content=if(r.error.type==FailureType.CONTRACT_MISSING)DeliveryOrdersUiState.Content.Unavailable("Assigned orders unavailable",r.error.message) else if(r.error.type==FailureType.UNAUTHORIZED)DeliveryOrdersUiState.Content.Error("Session expired","Please sign in again.") else DeliveryOrdersUiState.Content.Error("Unable to load orders",r.error.message))};if(r.error.type==FailureType.UNAUTHORIZED)_events.tryEmit(Event.SessionExpired)}}}}
 private val _events=MutableSharedFlow<Event>(extraBufferCapacity=1);val events:SharedFlow<Event> = _events.asSharedFlow()
}
