package com.daily.nexamartpartner.features.admin.presentation.viewmodel

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.daily.nexamartpartner.features.admin.domain.usecase.CancelAdminOrderUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetAdminOrderDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateAdminOrderStatusUseCase

class AdminOrderDetailsViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?,
    private val getAdminOrderDetailsUseCase: GetAdminOrderDetailsUseCase,
    private val updateAdminOrderStatusUseCase: UpdateAdminOrderStatusUseCase,
    private val cancelAdminOrderUseCase: CancelAdminOrderUseCase
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        @NonNull key: String,
        @NonNull modelClass: Class<T>,
        @NonNull handle: SavedStateHandle
    ): T {
        require(modelClass.isAssignableFrom(AdminOrderDetailsViewModel::class.java))
        return AdminOrderDetailsViewModel(
            savedStateHandle = handle,
            getAdminOrderDetailsUseCase = getAdminOrderDetailsUseCase,
            updateAdminOrderStatusUseCase = updateAdminOrderStatusUseCase,
            cancelAdminOrderUseCase = cancelAdminOrderUseCase
        ) as T
    }
}
