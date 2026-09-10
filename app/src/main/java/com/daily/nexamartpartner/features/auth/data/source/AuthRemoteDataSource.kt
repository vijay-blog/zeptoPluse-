package com.daily.nexamartpartner.features.auth.data.source

import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.auth.data.model.LoginResponseDto
import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationCredentials

interface AuthRemoteDataSource {
    suspend fun login(credentials: LoginCredentials): AppResult<LoginResponseDto>
    suspend fun register(credentials: RegistrationCredentials): AppResult<LoginResponseDto>
    suspend fun refresh(refreshToken: String): AppResult<LoginResponseDto>
    suspend fun logout(refreshToken: String?): AppResult<Unit>
}
