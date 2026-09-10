package com.daily.nexamartpartner.features.delivery.profile.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.delivery.profile.domain.usecase.*
class DeliveryPartnerProfileViewModelFactory(private val get:GetDeliveryPartnerProfileUseCase,private val update:UpdateDeliveryPartnerProfileUseCase):ViewModelProvider.Factory{override fun <T:ViewModel> create(modelClass:Class<T>):T=DeliveryPartnerProfileViewModel(get,update) as T}
