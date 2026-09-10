package com.daily.nexamartpartner.features.auth.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationCredentials
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import com.daily.nexamartpartner.features.auth.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(credentials: RegistrationCredentials): AppResult<UserSession> = repository.register(credentials)
}
