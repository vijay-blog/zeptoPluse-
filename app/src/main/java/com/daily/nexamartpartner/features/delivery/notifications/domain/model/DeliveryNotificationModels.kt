package com.daily.nexamartpartner.features.delivery.notifications.domain.model

data class DeliveryNotificationQuery(val page:Int=0,val pageSize:Int=20,val unreadOnly:Boolean=false)
data class DeliveryNotification(val id:String,val title:String,val message:String,val createdAt:String?,val read:Boolean,val type:String?,val orderId:String?,val actionUrl:String?)
data class DeliveryNotificationPage(val items:List<DeliveryNotification>,val page:Int,val pageSize:Int,val totalPages:Int,val totalElements:Long,val hasNextPage:Boolean,val unreadCount:Int?)
