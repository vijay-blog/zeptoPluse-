package com.daily.nexamartpartner.features.delivery.notifications.data.source
import com.daily.nexamartpartner.features.delivery.notifications.data.model.DeliveryNotificationPageDto
import retrofit2.Response
import retrofit2.http.*
interface DeliveryNotificationsApi{
 @GET suspend fun list(@Url path:String,@QueryMap query:Map<String,String>):Response<DeliveryNotificationPageDto>
 @POST suspend fun markRead(@Url path:String):Response<Unit>
 @POST suspend fun markAllRead(@Url path:String):Response<Unit>
}
