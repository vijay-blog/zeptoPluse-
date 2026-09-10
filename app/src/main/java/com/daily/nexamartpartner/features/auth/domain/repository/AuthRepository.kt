package com.daily.nexamartpartner.features.auth.domain.repository

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationCredentials
import com.daily.nexamartpartner.features.auth.domain.model.UserSession

interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): AppResult<UserSession>
    suspend fun register(credentials: RegistrationCredentials): AppResult<UserSession>
    suspend fun restoreSession(): AppResult<UserSession?>
    suspend fun logout(): AppResult<Unit>
    suspend fun refreshToken(): AppResult<UserSession>
}
