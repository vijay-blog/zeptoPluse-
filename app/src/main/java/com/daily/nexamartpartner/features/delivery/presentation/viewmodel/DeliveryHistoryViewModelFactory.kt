package com.daily.nexamartpartner.features.delivery.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.delivery.domain.usecase.GetDeliveryHistoryUseCase
class DeliveryHistoryViewModelFactory(private val useCase:GetDeliveryHistoryUseCase):ViewModelProvider.Factory{
 override fun <T:ViewModel> create(modelClass:Class<T>):T { if(modelClass.isAssignableFrom(DeliveryHistoryViewModel::class.java)) @Suppress("UNCHECKED_CAST") return DeliveryHistoryViewModel(useCase) as T; throw IllegalArgumentException("Unknown ViewModel") }
}
