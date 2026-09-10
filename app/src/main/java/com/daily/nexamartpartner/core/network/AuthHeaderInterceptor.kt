package com.daily.nexamartpartner.core.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthHeaderInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val encodedPath = original.url.encodedPath

        if (isPublicAuthPath(encodedPath)) {
            return chain.proceed(original)
        }

        val token = tokenProvider()
        if (token.isNullOrBlank()) {
            return chain.proceed(original)
        }

        val authenticatedRequest = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }

    private fun isPublicAuthPath(path: String): Boolean {
        return path.endsWith("/auth/login") ||
            path.endsWith("/auth/send-otp") ||
            path.endsWith("/auth/verify-otp") ||
            path.endsWith("/auth/refresh")
    }
}
