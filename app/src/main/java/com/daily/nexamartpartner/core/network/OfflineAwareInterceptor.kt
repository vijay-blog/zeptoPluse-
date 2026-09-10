package com.daily.nexamartpartner.core.network

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

/** Fails fast while the device is known to be offline; never queues or retries writes. */
class OfflineAwareInterceptor(
    private val connectivityMonitor: NetworkConnectivityMonitor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (connectivityMonitor.state.value == NetworkConnectivityMonitor.State.OFFLINE) {
            throw IOException("Device is offline")
        }
        return chain.proceed(chain.request())
    }
}
