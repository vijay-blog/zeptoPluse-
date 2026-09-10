package com.daily.nexamartpartner.features.delivery.availability.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.delivery.availability.domain.usecase.*

class DeliveryAvailabilityViewModelFactory(private val get: GetDeliveryAvailabilityUseCase, private val update: UpdateDeliveryAvailabilityUseCase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T = DeliveryAvailabilityViewModel(get, update) as T
}
