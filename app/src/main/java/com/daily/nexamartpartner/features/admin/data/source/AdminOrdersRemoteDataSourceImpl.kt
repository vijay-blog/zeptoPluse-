package com.daily.nexamartpartner.features.admin.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.data.contract.AdminOrdersContract
import com.daily.nexamartpartner.features.admin.data.model.AdminOrderDetailsDto
import com.daily.nexamartpartner.features.admin.data.model.AdminOrdersPageDto
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus

class AdminOrdersRemoteDataSourceImpl(
    private val api: AdminOrdersApi,
    private val contract: AdminOrdersContract,
    private val apiCallExecutor: ApiCallExecutor
) : AdminOrdersRemoteDataSource {

    override suspend fun getOrders(query: AdminOrdersQuery): AppResult<AdminOrdersPageDto> {
        val path = contract.listOrdersPath
            ?: return contractMissing("Order list API contract is not confirmed yet.")
        val params = contract.buildOrderListQuery(query)
            ?: return contractMissing("Order list query contract is not confirmed yet.")
        return apiCallExecutor.execute { api.getOrders(path, params) }
    }

    override suspend fun getOrderDetails(orderId: String): AppResult<AdminOrderDetailsDto> {
        val path = contract.resolvePath(contract.orderDetailsPathTemplate, orderId)
            ?: return contractMissing("Order details API contract is not confirmed yet.")
        return apiCallExecutor.execute { api.getOrderDetails(path) }
    }

    override suspend fun updateOrderStatus(orderId: String, targetStatus: OrderStatus): AppResult<Unit> {
        val path = contract.resolvePath(contract.updateStatusPathTemplate, orderId)
            ?: return contractMissing("Order status update API contract is not confirmed yet.")
        val body = contract.buildUpdateStatusBody(targetStatus)
            ?: return contractMissing("Order status update request contract is not confirmed yet.")
        return when (val result = apiCallExecutor.execute { api.updateOrderStatus(path, body) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
    }

    override suspend fun cancelOrder(orderId: String, reason: String?): AppResult<Unit> {
        val path = contract.resolvePath(contract.cancelOrderPathTemplate, orderId)
            ?: return contractMissing("Order cancellation API contract is not confirmed yet.")
        val body = contract.buildCancelOrderBody(reason)
            ?: return contractMissing("Order cancellation request contract is not confirmed yet.")
        return when (val result = apiCallExecutor.execute { api.cancelOrder(path, body) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
    }

    private fun <T> contractMissing(message: String): AppResult<T> {
        return AppResult.Failure(
            AppFailure(
                message = message,
                type = FailureType.CONTRACT_MISSING
            )
        )
    }
}
