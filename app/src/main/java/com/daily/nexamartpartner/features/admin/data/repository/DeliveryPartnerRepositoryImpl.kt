package com.daily.nexamartpartner.features.admin.data.repository

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.data.model.DeliveryPartnerDetailsDto
import com.daily.nexamartpartner.features.admin.data.model.DeliveryPartnerSummaryDto
import com.daily.nexamartpartner.features.admin.data.model.PartnerOrderSummaryDto
import com.daily.nexamartpartner.features.admin.data.source.DeliveryPartnerRemoteDataSource
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerDetails
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerStatistics
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerSummary
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnersQuery
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.model.PagedDeliveryPartners
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAccountStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAvailability
import com.daily.nexamartpartner.features.admin.domain.model.PartnerOrderSummary
import com.daily.nexamartpartner.features.admin.domain.model.PartnerVerificationStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerWorkState
import com.daily.nexamartpartner.features.admin.domain.repository.DeliveryPartnerRepository

class DeliveryPartnerRepositoryImpl(
    private val remote: DeliveryPartnerRemoteDataSource
) : DeliveryPartnerRepository {
    override suspend fun getPartners(query: DeliveryPartnersQuery): AppResult<PagedDeliveryPartners> =
        when (val result = remote.getPartners(query)) {
            is AppResult.Failure -> result
            is AppResult.Success -> {
                val partners = result.data.content?.map {
                    mapSummary(it) ?: return invalid("Invalid delivery partner list response.")
                }.orEmpty()
                val page = result.data.number ?: query.page
                val totalPages = result.data.totalPages ?: 1
                AppResult.Success(
                    PagedDeliveryPartners(
                        partners = partners,
                        page = page,
                        pageSize = result.data.size ?: query.pageSize,
                        totalPages = totalPages,
                        totalElements = result.data.totalElements ?: partners.size.toLong(),
                        hasNextPage = result.data.last?.not() ?: (page + 1 < totalPages)
                    )
                )
            }
        }

    override suspend fun getPartnerDetails(partnerId: String): AppResult<DeliveryPartnerDetails> =
        when (val result = remote.getPartnerDetails(partnerId)) {
            is AppResult.Failure -> result
            is AppResult.Success -> mapDetails(result.data)
        }

    override suspend fun performAction(
        partnerId: String,
        action: PartnerAdminAction,
        reason: String?
    ): AppResult<Unit> = remote.performAction(partnerId, action, reason)

    private fun mapSummary(dto: DeliveryPartnerSummaryDto): DeliveryPartnerSummary? {
        val id = dto.partnerId?.trim().takeUnless { it.isNullOrEmpty() } ?: return null
        val account = PartnerAccountStatus.fromRaw(dto.accountStatus)
        val verification = PartnerVerificationStatus.fromRaw(dto.verificationStatus)
        if (account == PartnerAccountStatus.UNKNOWN || verification == PartnerVerificationStatus.UNKNOWN) return null
        return DeliveryPartnerSummary(
            partnerId = id,
            name = dto.name?.trim().orEmpty(),
            phone = dto.phone?.trim(),
            profileImageUrl = dto.profileImageUrl?.trim(),
            accountStatus = account,
            verificationStatus = verification,
            availability = PartnerAvailability.fromRaw(dto.availability),
            workState = PartnerWorkState.fromRaw(dto.workState),
            activeDeliveries = dto.activeDeliveries,
            isAssignable = dto.isAssignable
        )
    }

    private fun mapDetails(dto: DeliveryPartnerDetailsDto): AppResult<DeliveryPartnerDetails> {
        val summary = mapSummary(
            DeliveryPartnerSummaryDto(
                dto.partnerId, dto.name, dto.phone, dto.profileImageUrl, dto.accountStatus,
                dto.verificationStatus, dto.availability, dto.workState,
                dto.statistics?.activeDeliveries?.toInt(), dto.isAssignable
            )
        ) ?: return invalid("Invalid delivery partner details response.")

        val actions = dto.allowedActions?.mapNotNull { raw ->
            PartnerAdminAction.entries.firstOrNull { it.backendValue.equals(raw.trim(), true) }
        }.orEmpty()
        return AppResult.Success(
            DeliveryPartnerDetails(
                partnerId = summary.partnerId,
                name = summary.name,
                phone = summary.phone,
                email = dto.email?.trim(),
                profileImageUrl = summary.profileImageUrl,
                accountStatus = summary.accountStatus,
                verificationStatus = summary.verificationStatus,
                availability = summary.availability,
                workState = summary.workState,
                registeredAt = dto.registeredAt?.trim(),
                lastActiveAt = dto.lastActiveAt?.trim(),
                vehicleType = dto.vehicleType?.trim(),
                vehicleNumber = dto.vehicleNumber?.trim(),
                licenseReference = dto.licenseReference?.trim(),
                statistics = dto.statistics?.let {
                    DeliveryPartnerStatistics(
                        it.totalDeliveries, it.completedDeliveries,
                        it.cancelledDeliveries, it.activeDeliveries
                    )
                },
                currentOrders = mapOrders(dto.currentOrders),
                recentHistory = mapOrders(dto.recentHistory),
                isAssignable = dto.isAssignable,
                allowedActions = actions
            )
        )
    }

    private fun mapOrders(dtos: List<PartnerOrderSummaryDto>?): List<PartnerOrderSummary> =
        dtos?.mapNotNull {
            val id = it.orderId?.trim().takeUnless(String?::isNullOrEmpty) ?: return@mapNotNull null
            val status = OrderStatus.fromRaw(it.status)
            if (status == OrderStatus.UNKNOWN) return@mapNotNull null
            PartnerOrderSummary(id, status, it.timestamp?.trim())
        }.orEmpty()

    private fun <T> invalid(message: String): AppResult<T> =
        AppResult.Failure(AppFailure(message, type = FailureType.SERVER))
}
