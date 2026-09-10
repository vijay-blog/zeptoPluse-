package com.daily.nexamartpartner.features.admin.data.source

import com.daily.nexamartpartner.features.admin.data.model.AdminDashboardResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface AdminDashboardApi {
    @GET
    suspend fun getDashboard(@Url endpointPath: String): Response<AdminDashboardResponseDto>
}
