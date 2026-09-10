package com.daily.nexamartpartner.features.admin.data.source

import com.daily.nexamartpartner.features.admin.data.model.AdminOrderDetailsDto
import com.daily.nexamartpartner.features.admin.data.model.AdminOrdersPageDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface AdminOrdersApi {
    @GET
    suspend fun getOrders(
        @Url endpointPath: String,
        @QueryMap queryParams: Map<String, String>
    ): Response<AdminOrdersPageDto>

    @GET
    suspend fun getOrderDetails(@Url endpointPath: String): Response<AdminOrderDetailsDto>

    @PATCH
    suspend fun updateOrderStatus(
        @Url endpointPath: String,
        @Body body: Map<String, String>
    ): Response<Unit>

    @POST
    suspend fun cancelOrder(
        @Url endpointPath: String,
        @Body body: Map<String, String>
    ): Response<Unit>
}
