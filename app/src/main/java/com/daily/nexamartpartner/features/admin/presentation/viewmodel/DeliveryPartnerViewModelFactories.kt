package com.daily.nexamartpartner.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.admin.domain.usecase.GetDeliveryPartnerDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetDeliveryPartnersUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateDeliveryPartnerUseCase

class DeliveryPartnerListViewModelFactory(
    private val getPartners: GetDeliveryPartnersUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(DeliveryPartnerListViewModel::class.java))
        return DeliveryPartnerListViewModel(getPartners) as T
    }
}

class DeliveryPartnerDetailsViewModelFactory(
    private val partnerId: String,
    private val getDetails: GetDeliveryPartnerDetailsUseCase,
    private val updatePartner: UpdateDeliveryPartnerUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(DeliveryPartnerDetailsViewModel::class.java))
        return DeliveryPartnerDetailsViewModel(partnerId, getDetails, updatePartner) as T
    }
}
