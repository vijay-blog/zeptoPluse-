package com.daily.nexamartpartner.core.network

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType

class PendingContractTokenRefreshHandler : TokenRefreshHandler {
    override suspend fun refreshAccessToken(): AppResult<String> {
        return AppResult.Failure(
            AppFailure(
                message = "Authentication service is not configured yet. Please contact support.",
                type = FailureType.CONTRACT_MISSING
            )
        )
    }
}
