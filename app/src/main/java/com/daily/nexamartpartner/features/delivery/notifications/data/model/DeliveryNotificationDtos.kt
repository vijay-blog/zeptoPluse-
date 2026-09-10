package com.daily.nexamartpartner.features.delivery.notifications.data.model

data class DeliveryNotificationDto(val id:String?,val title:String?,val message:String?,val createdAt:String?,val read:Boolean?,val type:String?,val orderId:String?,val actionUrl:String?)
data class DeliveryNotificationPageDto(val items:List<DeliveryNotificationDto>?,val page:Int?,val pageSize:Int?,val totalPages:Int?,val totalElements:Long?,val hasNextPage:Boolean?,val unreadCount:Int?)
