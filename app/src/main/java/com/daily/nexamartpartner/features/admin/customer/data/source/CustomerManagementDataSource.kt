package com.daily.nexamartpartner.features.admin.customer.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.*
import com.daily.nexamartpartner.features.admin.customer.data.contract.CustomerManagementContract
import com.daily.nexamartpartner.features.admin.customer.data.model.*
import com.daily.nexamartpartner.features.admin.customer.domain.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface CustomerManagementApi {
    @GET suspend fun list(@Url path: String, @QueryMap params: Map<String, String>): Response<CustomersPageDto>
    @GET suspend fun details(@Url path: String): Response<CustomerDto>
    @PATCH suspend fun action(@Url path: String, @Body body: Map<String, String>): Response<Unit>
}

interface CustomerManagementRemoteDataSource {
    suspend fun list(query: CustomerQuery): AppResult<CustomersPageDto>
    suspend fun details(customerId: String): AppResult<CustomerDto>
    suspend fun action(customerId: String, action: CustomerAdminAction): AppResult<Unit>
}

class CustomerManagementRemoteDataSourceImpl(
    private val api: CustomerManagementApi,
    private val contract: CustomerManagementContract,
    private val executor: ApiCallExecutor
) : CustomerManagementRemoteDataSource {
    private fun <T> missing(message: String): AppResult<T> = AppResult.Failure(AppFailure(message, type = FailureType.CONTRACT_MISSING))

    override suspend fun list(query: CustomerQuery): AppResult<CustomersPageDto> {
        val path = contract.listPath ?: return missing("Customer list API contract is not confirmed yet.")
        val params = contract.buildListQuery(query) ?: return missing("Customer list query contract is not confirmed yet.")
        return executor.execute { api.list(path, params) }
    }

    override suspend fun details(customerId: String): AppResult<CustomerDto> {
        val path = contract.resolvePath(contract.detailsPathTemplate, customerId)
            ?: return missing("Customer details API contract is not confirmed yet.")
        return executor.execute { api.details(path) }
    }

    override suspend fun action(customerId: String, action: CustomerAdminAction): AppResult<Unit> {
        val path = contract.resolvePath(contract.actionPathTemplate, customerId)
            ?: return missing("Customer action API contract is not confirmed yet.")
        val body = contract.buildActionBody(action)
            ?: return missing("Customer action request contract is not confirmed yet.")
        return when (val result = executor.execute { api.action(path, body) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
    }
}
