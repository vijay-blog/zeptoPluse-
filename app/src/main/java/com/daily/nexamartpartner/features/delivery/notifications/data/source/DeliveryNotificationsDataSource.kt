package com.daily.nexamartpartner.features.delivery.notifications.data.source
import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.notifications.data.contract.DeliveryNotificationsContract
import com.daily.nexamartpartner.features.delivery.notifications.data.model.*
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotificationQuery
class DeliveryNotificationsDataSource(private val api:DeliveryNotificationsApi,private val contract:DeliveryNotificationsContract,private val executor:ApiCallExecutor){
 suspend fun list(q:DeliveryNotificationQuery):AppResult<DeliveryNotificationPageDto>{val p=contract.listPath?:return AppResult.Failure(AppFailure("Delivery notifications API contract is not configured.",type=FailureType.CONTRACT_MISSING));val params=contract.buildListQuery(q)?:return AppResult.Failure(AppFailure("Delivery notifications query contract is not configured.",type=FailureType.CONTRACT_MISSING));return executor.execute{api.list(p,params)}}
 suspend fun markRead(id:String):AppResult<Unit>{val p=contract.buildMarkReadPath(id)?:return AppResult.Failure(AppFailure("Mark notification read API contract is not configured.",type=FailureType.CONTRACT_MISSING));return executor.execute{api.markRead(p)}}
 suspend fun markAllRead():AppResult<Unit>{val p=contract.markAllReadPath?:return AppResult.Failure(AppFailure("Mark all notifications read API contract is not configured.",type=FailureType.CONTRACT_MISSING));return executor.execute{api.markAllRead(p)}}
}
