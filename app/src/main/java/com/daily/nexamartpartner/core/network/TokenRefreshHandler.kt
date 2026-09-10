package com.daily.nexamartpartner.core.network

import com.daily.nexamartpartner.core.result.AppResult

interface TokenRefreshHandler {
    suspend fun refreshAccessToken(): AppResult<String>
}
