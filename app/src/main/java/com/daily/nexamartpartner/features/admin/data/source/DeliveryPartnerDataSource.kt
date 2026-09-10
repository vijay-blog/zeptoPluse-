package com.daily.nexamartpartner.features.admin.data.source

import com.daily.nexamartpartner.core.network.ApiCallExecutor
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.data.contract.DeliveryPartnerContract
import com.daily.nexamartpartner.features.admin.data.model.DeliveryPartnerDetailsDto
import com.daily.nexamartpartner.features.admin.data.model.DeliveryPartnersPageDto
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnersQuery
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface DeliveryPartnerApi {
    @GET
    suspend fun getPartners(
        @Url path: String,
        @QueryMap params: Map<String, String>
    ): Response<DeliveryPartnersPageDto>

    @GET
    suspend fun getPartnerDetails(@Url path: String): Response<DeliveryPartnerDetailsDto>

    @PATCH
    suspend fun performAction(
        @Url path: String,
        @Body body: Map<String, String>
    ): Response<Unit>
}

interface DeliveryPartnerRemoteDataSource {
    suspend fun getPartners(query: DeliveryPartnersQuery): AppResult<DeliveryPartnersPageDto>
    suspend fun getPartnerDetails(partnerId: String): AppResult<DeliveryPartnerDetailsDto>
    suspend fun performAction(
        partnerId: String,
        action: PartnerAdminAction,
        reason: String?
    ): AppResult<Unit>
}

class DeliveryPartnerRemoteDataSourceImpl(
    private val api: DeliveryPartnerApi,
    private val contract: DeliveryPartnerContract,
    private val executor: ApiCallExecutor
) : DeliveryPartnerRemoteDataSource {
    override suspend fun getPartners(query: DeliveryPartnersQuery): AppResult<DeliveryPartnersPageDto> {
        val path = contract.listPath ?: return contractMissing("Delivery partner list API is not confirmed.")
        val params = contract.buildListQuery(query)
            ?: return contractMissing("Delivery partner search/filter contract is not confirmed.")
        return executor.execute { api.getPartners(path, params) }
    }

    override suspend fun getPartnerDetails(partnerId: String): AppResult<DeliveryPartnerDetailsDto> {
        val path = contract.resolvePath(contract.detailsPathTemplate, partnerId)
            ?: return contractMissing("Delivery partner details API is not confirmed.")
        return executor.execute { api.getPartnerDetails(path) }
    }

    override suspend fun performAction(
        partnerId: String,
        action: PartnerAdminAction,
        reason: String?
    ): AppResult<Unit> {
        val path = contract.resolvePath(contract.actionPathTemplate, partnerId)
            ?: return contractMissing("Delivery partner action API is not confirmed.")
        val body = contract.buildActionBody(action, reason)
            ?: return contractMissing("Delivery partner action request contract is not confirmed.")
        return when (val result = executor.execute { api.performAction(path, body) }) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
    }

    private fun <T> contractMissing(message: String): AppResult<T> =
        AppResult.Failure(AppFailure(message, type = FailureType.CONTRACT_MISSING))
}
