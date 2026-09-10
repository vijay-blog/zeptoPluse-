package com.daily.nexamartpartner.features.delivery.notifications.presentation.viewmodel
import androidx.lifecycle.*
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotificationQuery
import com.daily.nexamartpartner.features.delivery.notifications.domain.usecase.*
import com.daily.nexamartpartner.features.delivery.notifications.presentation.state.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class DeliveryNotificationsViewModel(private val get: GetDeliveryNotificationsUseCase,private val read:MarkDeliveryNotificationReadUseCase,private val readAll:MarkAllDeliveryNotificationsReadUseCase):ViewModel(){
 sealed interface Event{data object SessionExpired:Event}
 private val _state=MutableStateFlow(DeliveryNotificationsUiState());val state:StateFlow<DeliveryNotificationsUiState> = _state.asStateFlow();private val _events=MutableSharedFlow<Event>(extraBufferCapacity=1);val events=_events.asSharedFlow();private val items=mutableListOf<com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotification>();private var page=0;private var next=true;private var busy=false
 init{load(false)}
 fun refresh(){if(busy)return;_state.update{it.copy(isRefreshing=true)};load(false)}
 fun retry(){load(true)}
 fun nextPage(){if(busy||!next)return;_state.update{it.copy(isLoadingMore=true)};fetch(page+1,true)}
 fun markRead(id:String){viewModelScope.launch{when(val r=read(id)){is AppResult.Success->{val i=items.indexOfFirst{it.id==id};if(i>=0){items[i]=items[i].copy(read=true);_state.update{it.copy(unreadCount=items.count{!it.read},content=NotificationContent.Success(items.toList(),next))}}};is AppResult.Failure->if(r.error.type==FailureType.UNAUTHORIZED)_events.tryEmit(Event.SessionExpired)}}}
 fun markAllRead(){viewModelScope.launch{when(val r=readAll()){is AppResult.Success->{items.indices.forEach{i->items[i]=items[i].copy(read=true)};_state.update{it.copy(unreadCount=0,content=NotificationContent.Success(items.toList(),next))}};is AppResult.Failure->if(r.error.type==FailureType.UNAUTHORIZED)_events.tryEmit(Event.SessionExpired)}}}
 private fun load(force:Boolean){if(force)_state.update{it.copy(content=NotificationContent.Loading)};fetch(0,false)}
 private fun fetch(p:Int,append:Boolean){if(busy)return;busy=true;viewModelScope.launch{when(val r=get(DeliveryNotificationQuery(p,20))){is AppResult.Success->{if(!append)items.clear();items.addAll(r.data.items);page=r.data.page;next=r.data.hasNextPage;_state.update{it.copy(isRefreshing=false,isLoadingMore=false,unreadCount=r.data.unreadCount?:items.count{!it.read},content=if(items.isEmpty())NotificationContent.Empty("You're all caught up","New delivery updates will appear here.") else NotificationContent.Success(items.toList(),next))}};is AppResult.Failure->{_state.update{it.copy(isRefreshing=false,isLoadingMore=false,content=if(r.error.type==FailureType.CONTRACT_MISSING)NotificationContent.Unavailable("Notifications unavailable",r.error.message) else NotificationContent.Error(if(r.error.type==FailureType.UNAUTHORIZED)"Session expired" else "Unable to load notifications",r.error.message))};if(r.error.type==FailureType.UNAUTHORIZED)_events.tryEmit(Event.SessionExpired)}};busy=false}}
}
