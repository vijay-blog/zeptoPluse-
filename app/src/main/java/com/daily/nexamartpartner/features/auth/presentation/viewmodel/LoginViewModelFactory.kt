package com.daily.nexamartpartner.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.auth.domain.usecase.LoginUseCase
import com.daily.nexamartpartner.features.auth.presentation.state.AuthStateStore

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val authStateStore: AuthStateStore
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginUseCase = loginUseCase,
                authStateStore = authStateStore
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
