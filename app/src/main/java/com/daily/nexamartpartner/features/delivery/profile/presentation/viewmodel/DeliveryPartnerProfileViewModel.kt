package com.daily.nexamartpartner.features.delivery.profile.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.profile.domain.model.*
import com.daily.nexamartpartner.features.delivery.profile.domain.usecase.*
import com.daily.nexamartpartner.features.delivery.profile.presentation.state.DeliveryPartnerProfileUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeliveryPartnerProfileViewModel(private val get: GetDeliveryPartnerProfileUseCase, private val update: UpdateDeliveryPartnerProfileUseCase):ViewModel(){
 private val _state=MutableStateFlow(DeliveryPartnerProfileUiState()); val state:StateFlow<DeliveryPartnerProfileUiState> = _state.asStateFlow()
 private val _events=MutableSharedFlow<Event>(); val events=_events.asSharedFlow()
 sealed interface Event{data object SessionExpired:Event; data class Message(val text:String):Event}
 init{load()}
 fun load(){viewModelScope.launch{_state.update{it.copy(loading=true,error=null,unavailable=false)}; when(val r=get()){is AppResult.Success->_state.value=DeliveryPartnerProfileUiState(loading=false,profile=r.data);is AppResult.Failure->handle(r)}}}
 fun save(u:DeliveryPartnerProfileUpdate){viewModelScope.launch{_state.update{it.copy(saving=true,error=null)};when(val r=update(u)){is AppResult.Success->{_state.value=DeliveryPartnerProfileUiState(profile=r.data);_events.emit(Event.Message("Profile updated successfully."))};is AppResult.Failure->handle(r, saving=false)}}}
 private suspend fun handle(r:AppResult.Failure,saving:Boolean=false){when(r.error.type){FailureType.UNAUTHORIZED->{_events.emit(Event.SessionExpired);_state.update{it.copy(loading=false,saving=saving)}};FailureType.CONTRACT_MISSING->_state.update{it.copy(loading=false,saving=saving,unavailable=true)};else->_state.update{it.copy(loading=false,saving=saving,error=r.error.message)}}}
}
