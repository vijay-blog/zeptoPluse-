package com.daily.nexamartpartner.features.delivery.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.delivery.data.contract.DeliveryOrderWorkflowContract
import com.daily.nexamartpartner.features.delivery.data.model.*
import com.daily.nexamartpartner.features.delivery.domain.model.*

interface DeliveryOrderWorkflowDataSource {
 suspend fun getAssignedOrders(q:DeliveryOrdersQuery):AppResult<DeliveryOrdersPageDto>
 suspend fun getHistoryOrders(q:DeliveryOrdersQuery):AppResult<DeliveryOrdersPageDto>
 suspend fun getDetails(id:String):AppResult<DeliveryOrderDetailsDto>
 suspend fun performAction(id:String,a:DeliveryOrderAction):AppResult<Unit>
}

class DeliveryOrderWorkflowDataSourceImpl(private val api:DeliveryOrderWorkflowApi, private val contract:DeliveryOrderWorkflowContract, private val executor:ApiCallExecutor):DeliveryOrderWorkflowDataSource {
 override suspend fun getAssignedOrders(q:DeliveryOrdersQuery):AppResult<DeliveryOrdersPageDto>{
  val p=contract.listAssignedOrdersPath ?: return missing("Assigned-orders API contract is not confirmed yet.")
  val params=contract.buildListQuery(q) ?: return missing("Assigned-orders query contract is not confirmed yet.")
  return executor.execute{api.getAssignedOrders(p,params)}
 }
 override suspend fun getHistoryOrders(q:DeliveryOrdersQuery):AppResult<DeliveryOrdersPageDto>{
  val p=contract.listHistoryOrdersPath ?: return missing("Delivery-history API contract is not confirmed yet.")
  val params=contract.buildListQuery(q) ?: return missing("Delivery-history query contract is not confirmed yet.")
  return executor.execute{api.getAssignedOrders(p,params)}
 }
 override suspend fun getDetails(id:String):AppResult<DeliveryOrderDetailsDto>{
  val p=contract.resolvePath(contract.orderDetailsPathTemplate,id) ?: return missing("Delivery order details API contract is not confirmed yet.")
  return executor.execute{api.getOrderDetails(p)}
 }
 override suspend fun performAction(id:String,a:DeliveryOrderAction):AppResult<Unit>{
  val p=contract.resolvePath(contract.actionPathTemplate,id) ?: return missing("Delivery order action API contract is not confirmed yet.")
  val b=contract.buildActionBody(a) ?: return missing("Delivery order action request contract is not confirmed yet.")
  return when(val r=executor.execute{api.performAction(p,b)}){is AppResult.Success->AppResult.Success(Unit);is AppResult.Failure->r}
 }
 private fun <T> missing(m:String):AppResult<T> = AppResult.Failure(AppFailure(m,type=FailureType.CONTRACT_MISSING))
}
