package com.daily.nexamartpartner.features.auth.data.source

import com.daily.nexamartpartner.features.auth.data.model.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<LoginResponseDto>

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<LoginResponseDto>

    @POST("auth/refresh")
    suspend fun refresh(@Body body: Map<String, String>): Response<LoginResponseDto>

    @POST("auth/logout")
    suspend fun logout(@Body body: Map<String, String>): Response<Unit>
}
