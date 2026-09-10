package com.daily.nexamartpartner.core.network

import com.daily.nexamartpartner.core.result.AppResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SingleFlightTokenRefreshHandler(
    private val delegate: TokenRefreshHandler
) : TokenRefreshHandler {
    private val lock = Mutex()

    override suspend fun refreshAccessToken(): AppResult<String> {
        return lock.withLock { delegate.refreshAccessToken() }
    }
}
