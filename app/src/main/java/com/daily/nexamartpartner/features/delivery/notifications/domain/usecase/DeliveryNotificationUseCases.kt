package com.daily.nexamartpartner.features.delivery.notifications.domain.usecase
import com.daily.nexamartpartner.features.delivery.notifications.domain.repository.DeliveryNotificationsRepository
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotificationQuery
class GetDeliveryNotificationsUseCase(private val r:DeliveryNotificationsRepository){suspend operator fun invoke(q:DeliveryNotificationQuery)=r.list(q)}
class MarkDeliveryNotificationReadUseCase(private val r:DeliveryNotificationsRepository){suspend operator fun invoke(id:String)=r.markRead(id)}
class MarkAllDeliveryNotificationsReadUseCase(private val r:DeliveryNotificationsRepository){suspend operator fun invoke()=r.markAllRead()}
