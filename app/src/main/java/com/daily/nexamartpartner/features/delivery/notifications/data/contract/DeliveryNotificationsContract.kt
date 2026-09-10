package com.daily.nexamartpartner.features.delivery.notifications.data.contract
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotificationQuery
interface DeliveryNotificationsContract {
 val listPath:String?
 val markReadPath:String?
 val markAllReadPath:String?
 fun buildListQuery(q:DeliveryNotificationQuery):Map<String,String>?
 fun buildMarkReadPath(id:String):String? = markReadPath?.replace("{id}",id)
}
class PendingBackendDeliveryNotificationsContract:DeliveryNotificationsContract{
 override val listPath:String?=null; override val markReadPath:String?=null; override val markAllReadPath:String?=null
 override fun buildListQuery(q:DeliveryNotificationQuery):Map<String,String>?=null
}
