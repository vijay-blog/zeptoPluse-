package com.daily.nexamartpartner.features.delivery.earnings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.delivery.earnings.domain.usecase.*

class DeliveryEarningsViewModelFactory(private val summary: GetDeliveryEarningsSummaryUseCase, private val history: GetDeliveryEarningsHistoryUseCase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T = DeliveryEarningsViewModel(summary, history) as T
}
