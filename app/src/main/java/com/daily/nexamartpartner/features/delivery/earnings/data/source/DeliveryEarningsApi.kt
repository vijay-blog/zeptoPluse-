package com.daily.nexamartpartner.features.delivery.earnings.data.source

import com.daily.nexamartpartner.features.delivery.earnings.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface DeliveryEarningsApi {
    @GET suspend fun getSummary(@Url path: String, @QueryMap query: Map<String, String>): Response<DeliveryEarningsSummaryDto>
    @GET suspend fun getHistory(@Url path: String, @QueryMap query: Map<String, String>): Response<DeliveryEarningsPageDto>
}
