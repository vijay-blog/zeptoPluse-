package com.daily.nexamartpartner.features.delivery.presentation.viewmodel

import androidx.lifecycle.*
import com.daily.nexamartpartner.features.delivery.domain.usecase.GetAssignedDeliveryOrdersUseCase

class DeliveryOrdersViewModelFactory(private val useCase:GetAssignedDeliveryOrdersUseCase):ViewModelProvider.Factory{override fun <T:ViewModel>create(modelClass:Class<T>):T{if(!modelClass.isAssignableFrom(DeliveryOrdersViewModel::class.java))throw IllegalArgumentException();return DeliveryOrdersViewModel(useCase) as T}}
