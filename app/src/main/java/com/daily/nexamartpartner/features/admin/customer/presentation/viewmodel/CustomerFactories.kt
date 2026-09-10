package com.daily.nexamartpartner.features.admin.customer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.admin.customer.domain.usecase.*

class CustomerListViewModelFactory(private val get: GetCustomersUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CustomerListViewModel(get) as T
}
class CustomerDetailsViewModelFactory(private val id: String, private val get: GetCustomerDetailsUseCase, private val action: PerformCustomerAdminActionUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CustomerDetailsViewModel(id, get, action) as T
}
