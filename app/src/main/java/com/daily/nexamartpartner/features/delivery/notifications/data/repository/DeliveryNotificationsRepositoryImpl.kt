package com.daily.nexamartpartner.features.delivery.notifications.data.repository
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.notifications.data.source.DeliveryNotificationsDataSource
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.*
import com.daily.nexamartpartner.features.delivery.notifications.domain.repository.DeliveryNotificationsRepository
class DeliveryNotificationsRepositoryImpl(private val source:DeliveryNotificationsDataSource):DeliveryNotificationsRepository{
 override suspend fun list(q:DeliveryNotificationQuery):AppResult<DeliveryNotificationPage>{return when(val r=source.list(q)){is AppResult.Success->{val d=r.data;AppResult.Success(DeliveryNotificationPage((d.items?:emptyList()).map{DeliveryNotification(it.id.orEmpty(),it.title.orEmpty(),it.message.orEmpty(),it.createdAt,it.read?:false,it.type,it.orderId,it.actionUrl)} ,d.page?:q.page,d.pageSize?:q.pageSize,d.totalPages?:0,d.totalElements?:0L,d.hasNextPage?:false,d.unreadCount))};is AppResult.Failure->r}}
 override suspend fun markRead(id:String)=source.markRead(id)
 override suspend fun markAllRead()=source.markAllRead()
}
