package com.daily.nexamartpartner.features.auth.data.repository

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.auth.data.model.LoginResponseDto
import com.daily.nexamartpartner.features.auth.data.source.AuthRemoteDataSource
import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationCredentials
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import com.daily.nexamartpartner.features.auth.domain.repository.AuthRepository
import com.daily.nexamartpartner.features.auth.domain.session.SessionManager

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun login(credentials: LoginCredentials): AppResult<UserSession> = when (val result = remoteDataSource.login(credentials)) {
        is AppResult.Success -> mapAndPersistSession(result.data)
        is AppResult.Failure -> result
    }

    override suspend fun register(credentials: RegistrationCredentials): AppResult<UserSession> = when (val result = remoteDataSource.register(credentials)) {
        is AppResult.Success -> mapAndPersistSession(result.data)
        is AppResult.Failure -> result
    }

    override suspend fun restoreSession(): AppResult<UserSession?> = try {
        AppResult.Success(sessionManager.initialize())
    } catch (throwable: Throwable) {
        AppResult.Failure(AppFailure(throwable.message ?: "Unable to restore session.", type = FailureType.UNKNOWN))
    }

    override suspend fun logout(): AppResult<Unit> {
        val refresh = sessionManager.currentSession.value?.refreshToken
        remoteDataSource.logout(refresh)
        sessionManager.clearSession()
        return AppResult.Success(Unit)
    }

    override suspend fun refreshToken(): AppResult<UserSession> {
        val existing = sessionManager.currentSession.value ?: return AppResult.Failure(AppFailure("Your session has expired. Please sign in again.", type = FailureType.UNAUTHORIZED))
        return when (val result = remoteDataSource.refresh(existing.refreshToken)) {
            is AppResult.Success -> mapAndPersistSession(result.data)
            is AppResult.Failure -> { sessionManager.clearSession(); result }
        }
    }

    private suspend fun mapAndPersistSession(dto: LoginResponseDto): AppResult<UserSession> {
        val user = dto.user ?: return AppResult.Failure(AppFailure("Invalid authentication response from server.", type = FailureType.SERVER))
        val access = dto.accessToken ?: return AppResult.Failure(AppFailure("Invalid authentication response from server.", type = FailureType.SERVER))
        val refresh = dto.refreshToken ?: return AppResult.Failure(AppFailure("Invalid authentication response from server.", type = FailureType.SERVER))
        val id = user.id ?: return AppResult.Failure(AppFailure("Invalid authentication response from server.", type = FailureType.SERVER))
        val role = UserRole.fromRaw(user.role)
        if (role == UserRole.UNSUPPORTED) return AppResult.Failure(AppFailure("This application is only for Admin and Delivery Partners.", type = FailureType.UNSUPPORTED_ROLE))
        val session = UserSession(access, refresh, id, user.name.orEmpty(), user.phone ?: user.email, role)
        sessionManager.saveSession(session)
        return AppResult.Success(session)
    }
}
