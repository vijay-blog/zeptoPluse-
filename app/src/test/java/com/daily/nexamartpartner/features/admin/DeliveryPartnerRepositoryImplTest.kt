package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.data.model.DeliveryPartnerDetailsDto
import com.daily.nexamartpartner.features.admin.data.model.DeliveryPartnerSummaryDto
import com.daily.nexamartpartner.features.admin.data.model.DeliveryPartnersPageDto
import com.daily.nexamartpartner.features.admin.data.repository.DeliveryPartnerRepositoryImpl
import com.daily.nexamartpartner.features.admin.data.source.DeliveryPartnerRemoteDataSource
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerFilters
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnersQuery
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAccountStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeliveryPartnerRepositoryImplTest {
    @Test
    fun `list success maps partner and pagination`() = runTest {
        val repository = DeliveryPartnerRepositoryImpl(
            FakeRemote(
                listResult = AppResult.Success(
                    DeliveryPartnersPageDto(
                        listOf(summaryDto()), 0, 20, 2, 21, false
                    )
                )
            )
        )
        val result = repository.getPartners(query())
        assertTrue(result is AppResult.Success)
        val page = (result as AppResult.Success).data
        assertEquals(1, page.partners.size)
        assertEquals(PartnerAccountStatus.ACTIVE, page.partners.first().accountStatus)
        assertTrue(page.hasNextPage)
    }

    @Test
    fun `empty list maps successfully`() = runTest {
        val repository = DeliveryPartnerRepositoryImpl(
            FakeRemote(AppResult.Success(DeliveryPartnersPageDto(emptyList(), 0, 20, 0, 0, true)))
        )
        val result = repository.getPartners(query())
        assertTrue(result is AppResult.Success)
        assertTrue((result as AppResult.Success).data.partners.isEmpty())
    }

    @Test
    fun `search filter pagination query reaches remote`() = runTest {
        val remote = FakeRemote(AppResult.Success(DeliveryPartnersPageDto(emptyList(), 1, 10, 2, 15, true)))
        val repository = DeliveryPartnerRepositoryImpl(remote)
        val query = DeliveryPartnersQuery(
            1, 10, "Ravi", DeliveryPartnerFilters(accountStatus = PartnerAccountStatus.ACTIVE)
        )
        repository.getPartners(query)
        assertEquals(query, remote.lastQuery)
    }

    @Test
    fun `network unauthorized forbidden and server failures propagate`() = runTest {
        listOf(
            FailureType.NETWORK to null,
            FailureType.UNAUTHORIZED to 401,
            FailureType.FORBIDDEN to 403,
            FailureType.SERVER to 500
        ).forEach { (type, code) ->
            val repository = DeliveryPartnerRepositoryImpl(
                FakeRemote(AppResult.Failure(AppFailure("Failure", code, type)))
            )
            val result = repository.getPartners(query())
            assertEquals(type, (result as AppResult.Failure).error.type)
        }
    }

    @Test
    fun `not found and conflict propagate`() = runTest {
        val notFound = DeliveryPartnerRepositoryImpl(
            FakeRemote(
                AppResult.Success(DeliveryPartnersPageDto(emptyList(), 0, 20, 0, 0, true)),
                detailsResult = AppResult.Failure(AppFailure("Not found", 404))
            )
        ).getPartnerDetails("missing")
        assertEquals(404, (notFound as AppResult.Failure).error.code)

        val conflict = DeliveryPartnerRepositoryImpl(
            FakeRemote(
                AppResult.Success(DeliveryPartnersPageDto(emptyList(), 0, 20, 0, 0, true)),
                actionResult = AppResult.Failure(AppFailure("Conflict", 409))
            )
        ).performAction("DP1", PartnerAdminAction.DEACTIVATE, null)
        assertEquals(409, (conflict as AppResult.Failure).error.code)
    }

    private fun query() = DeliveryPartnersQuery(0, 20, null, DeliveryPartnerFilters())

    private fun summaryDto() = DeliveryPartnerSummaryDto(
        "DP1", "Partner", "9999999999", null, "ACTIVE", "VERIFIED",
        "ONLINE", "AVAILABLE", 1, true
    )

    private class FakeRemote(
        private val listResult: AppResult<DeliveryPartnersPageDto>,
        private val detailsResult: AppResult<DeliveryPartnerDetailsDto> =
            AppResult.Failure(AppFailure("Not used")),
        private val actionResult: AppResult<Unit> = AppResult.Success(Unit)
    ) : DeliveryPartnerRemoteDataSource {
        var lastQuery: DeliveryPartnersQuery? = null
        override suspend fun getPartners(query: DeliveryPartnersQuery): AppResult<DeliveryPartnersPageDto> {
            lastQuery = query
            return listResult
        }
        override suspend fun getPartnerDetails(partnerId: String) = detailsResult
        override suspend fun performAction(
            partnerId: String,
            action: PartnerAdminAction,
            reason: String?
        ) = actionResult
    }
}
