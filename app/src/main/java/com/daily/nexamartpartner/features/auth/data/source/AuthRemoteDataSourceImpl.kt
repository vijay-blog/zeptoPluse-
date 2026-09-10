package com.daily.nexamartpartner.features.auth.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.auth.data.contract.AuthRequestContract
import com.daily.nexamartpartner.features.auth.data.contract.RegistrationRequestContract
import com.daily.nexamartpartner.features.auth.data.model.LoginResponseDto
import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials
import com.daily.nexamartpartner.features.auth.domain.model.RegistrationCredentials

class AuthRemoteDataSourceImpl(
    private val api: AuthApi,
    private val requestContract: AuthRequestContract,
    private val registrationContract: RegistrationRequestContract,
    private val apiCallExecutor: ApiCallExecutor
) : AuthRemoteDataSource {
    override suspend fun login(credentials: LoginCredentials): AppResult<LoginResponseDto> {
        val response = apiCallExecutor.execute { api.login(requestContract.buildLoginBody(credentials)) }
        return mapAuthFailure(response)
    }

    override suspend fun register(credentials: RegistrationCredentials): AppResult<LoginResponseDto> {
        val response = apiCallExecutor.execute { api.register(registrationContract.buildBody(credentials)) }
        return mapAuthFailure(response)
    }

    override suspend fun refresh(refreshToken: String): AppResult<LoginResponseDto> =
        apiCallExecutor.execute { api.refresh(requestContract.buildRefreshBody(refreshToken)) }

    override suspend fun logout(refreshToken: String?): AppResult<Unit> =
        apiCallExecutor.execute { api.logout(mapOf("refreshToken" to (refreshToken ?: ""))) }.let {
            when (it) { is AppResult.Success -> AppResult.Success(Unit); is AppResult.Failure -> it }
        }

    private fun mapAuthFailure(result: AppResult<LoginResponseDto>): AppResult<LoginResponseDto> {
        if (result is AppResult.Failure && result.error.code == 401) {
            return AppResult.Failure(result.error.copy(message = "Invalid email or password.", type = FailureType.UNAUTHORIZED))
        }
        return result
    }
}
