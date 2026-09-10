package com.daily.nexamartpartner.features.delivery.presentation.viewmodel

import androidx.lifecycle.*
import com.daily.nexamartpartner.features.delivery.domain.usecase.*

class DeliveryOrderDetailsViewModelFactory(private val id:String,private val get:GetDeliveryOrderDetailsUseCase,private val action:PerformDeliveryOrderActionUseCase):ViewModelProvider.Factory{override fun <T:ViewModel>create(modelClass:Class<T>):T{if(!modelClass.isAssignableFrom(DeliveryOrderDetailsViewModel::class.java))throw IllegalArgumentException();return DeliveryOrderDetailsViewModel(id,get,action) as T}}
