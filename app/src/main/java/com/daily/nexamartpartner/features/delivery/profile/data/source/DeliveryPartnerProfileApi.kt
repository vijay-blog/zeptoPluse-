package com.daily.nexamartpartner.features.delivery.profile.data.source

import com.daily.nexamartpartner.features.delivery.profile.data.model.DeliveryPartnerProfileDto
import retrofit2.Response
import retrofit2.http.*

interface DeliveryPartnerProfileApi {
    @GET
    suspend fun getProfile(@Url path: String): Response<DeliveryPartnerProfileDto>
    @PUT
    suspend fun updateProfile(@Url path: String, @Body body: Any): Response<DeliveryPartnerProfileDto>
}
