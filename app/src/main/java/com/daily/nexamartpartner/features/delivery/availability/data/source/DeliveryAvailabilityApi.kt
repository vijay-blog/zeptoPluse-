package com.daily.nexamartpartner.features.delivery.availability.data.source

import com.daily.nexamartpartner.features.delivery.availability.data.model.DeliveryAvailabilityDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Url

interface DeliveryAvailabilityApi {
    @GET suspend fun get(@Url path: String): Response<DeliveryAvailabilityDto>
    @PUT suspend fun update(@Url path: String, @Body body: Any): Response<DeliveryAvailabilityDto>
}
