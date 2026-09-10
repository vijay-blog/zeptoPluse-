package com.daily.nexamartpartner.features.auth.domain.usecase

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import com.daily.nexamartpartner.features.auth.domain.repository.AuthRepository

class RestoreSessionUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): AppResult<UserSession?> = repository.restoreSession()
}
