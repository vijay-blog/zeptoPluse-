package com.daily.nexamartpartner.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.auth.domain.usecase.LogoutUseCase
import com.daily.nexamartpartner.features.auth.domain.usecase.RestoreSessionUseCase
import com.daily.nexamartpartner.features.auth.presentation.state.AuthStateStore

class AuthCoordinatorViewModelFactory(
    private val restoreSessionUseCase: RestoreSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authStateStore: AuthStateStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthCoordinatorViewModel::class.java)) {
            return AuthCoordinatorViewModel(
                restoreSessionUseCase = restoreSessionUseCase,
                logoutUseCase = logoutUseCase,
                authStateStore = authStateStore
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
