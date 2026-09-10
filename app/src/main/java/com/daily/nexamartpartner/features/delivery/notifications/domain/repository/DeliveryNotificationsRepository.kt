package com.daily.nexamartpartner.features.delivery.notifications.domain.repository
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.*
interface DeliveryNotificationsRepository{ suspend fun list(q:DeliveryNotificationQuery):AppResult<DeliveryNotificationPage>;suspend fun markRead(id:String):AppResult<Unit>;suspend fun markAllRead():AppResult<Unit> }
