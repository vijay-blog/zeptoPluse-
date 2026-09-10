package com.daily.nexamartpartner.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrdersUseCase

class AdminOrdersViewModelFactory(
    private val getAdminOrdersUseCase: GetAdminOrdersUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AdminOrdersViewModel::class.java))
        return AdminOrdersViewModel(getAdminOrdersUseCase) as T
    }
}
