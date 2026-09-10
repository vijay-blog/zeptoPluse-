package com.daily.nexamartpartner.core.network

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

/** Retries only idempotent reads after a transport failure. Mutating requests are never retried. */
class SafeReadRetryInterceptor(private val maxRetries: Int = 1) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.method != "GET" && request.method != "HEAD") {
            return chain.proceed(request)
        }

        var attempt = 0
        while (true) {
            try {
                return chain.proceed(request)
            } catch (error: IOException) {
                if (attempt++ >= maxRetries) throw error
            }
        }
    }
}
