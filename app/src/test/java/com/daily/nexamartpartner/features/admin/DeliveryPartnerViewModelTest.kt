package com.daily.nexamartpartner.features.admin

import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerDetails
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerFilters
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerSummary
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnersQuery
import com.daily.nexamartpartner.features.admin.domain.model.PagedDeliveryPartners
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAccountStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAvailability
import com.daily.nexamartpartner.features.admin.domain.model.PartnerVerificationStatus
import com.daily.nexamartpartner.features.admin.domain.model.PartnerWorkState
import com.daily.nexamartpartner.features.admin.domain.repository.DeliveryPartnerRepository
import com.daily.nexamartpartner.features.admin.domain.usecase.GetDeliveryPartnerDetailsUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.GetDeliveryPartnersUseCase
import com.daily.nexamartpartner.features.admin.domain.usecase.UpdateDeliveryPartnerUseCase
import com.daily.nexamartpartner.features.admin.presentation.state.DeliveryPartnerDetailsUiState
import com.daily.nexamartpartner.features.admin.presentation.state.DeliveryPartnerListUiState
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.DeliveryPartnerDetailsViewModel
import com.daily.nexamartpartner.features.admin.presentation.viewmodel.DeliveryPartnerListViewModel
import com.daily.nexamartpartner.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryPartnerViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test
    fun `list initial success empty and error states`() = runTest {
        val successVm = listVm(mutableListOf(AppResult.Success(page(listOf(summary())))))
        advanceUntilIdle()
        assertTrue(successVm.uiState.value.content is DeliveryPartnerListUiState.Content.Success)

        val emptyVm = listVm(mutableListOf(AppResult.Success(page(emptyList()))))
        advanceUntilIdle()
        assertTrue(emptyVm.uiState.value.content is DeliveryPartnerListUiState.Content.Empty)

        val errorVm = listVm(
            mutableListOf(AppResult.Failure(AppFailure("Network", type = FailureType.NETWORK)))
        )
        advanceUntilIdle()
        assertTrue(errorVm.uiState.value.content is DeliveryPartnerListUiState.Content.Error)
    }

    @Test
    fun `search filter refresh and pagination request server`() = runTest {
        val repo = FakeRepository(
            listResults = mutableListOf(
                AppResult.Success(page(listOf(summary()), hasNext = true)),
                AppResult.Success(page(listOf(summary("DP2")))),
                AppResult.Success(page(listOf(summary("DP3")))),
                AppResult.Success(page(listOf(summary("DP4")), pageNumber = 1))
            )
        )
        val vm = DeliveryPartnerListViewModel(GetDeliveryPartnersUseCase(repo))
        advanceUntilIdle()
        vm.search("Partner")
        advanceUntilIdle()
        vm.applyFilters(DeliveryPartnerFilters(accountStatus = PartnerAccountStatus.ACTIVE))
        advanceUntilIdle()
        vm.refresh()
        advanceUntilIdle()
        vm.loadNext()
        advanceUntilIdle()
        assertEquals(4, repo.queries.size)
        assertEquals("Partner", repo.queries[1].searchText)
        assertEquals(PartnerAccountStatus.ACTIVE, repo.queries[2].filters.accountStatus)
    }

    @Test
    fun `details load and all backend allowed actions execute`() = runTest {
        PartnerAdminAction.entries.forEach { action ->
            val repo = FakeRepository(
                detailsResults = mutableListOf(
                    AppResult.Success(details(listOf(action))),
                    AppResult.Success(details(emptyList()))
                )
            )
            val vm = DeliveryPartnerDetailsViewModel(
                "DP1",
                GetDeliveryPartnerDetailsUseCase(repo),
                UpdateDeliveryPartnerUseCase(repo)
            )
            advanceUntilIdle()
            vm.performAction(action)
            advanceUntilIdle()
            assertEquals(action, repo.actions.single())
            assertTrue(vm.uiState.value.content is DeliveryPartnerDetailsUiState.Content.Success)
        }
    }

    private fun listVm(results: MutableList<AppResult<PagedDeliveryPartners>>) =
        DeliveryPartnerListViewModel(GetDeliveryPartnersUseCase(FakeRepository(results)))

    private fun page(
        values: List<DeliveryPartnerSummary>,
        hasNext: Boolean = false,
        pageNumber: Int = 0
    ) = PagedDeliveryPartners(values, pageNumber, 20, if (hasNext) 2 else 1, values.size.toLong(), hasNext)

    private fun summary(id: String = "DP1") = DeliveryPartnerSummary(
        id, "Partner", "9999999999", null, PartnerAccountStatus.ACTIVE,
        PartnerVerificationStatus.VERIFIED, PartnerAvailability.ONLINE,
        PartnerWorkState.AVAILABLE, 1, true
    )

    private fun details(actions: List<PartnerAdminAction>) = DeliveryPartnerDetails(
        "DP1", "Partner", "9999999999", null, null, PartnerAccountStatus.ACTIVE,
        PartnerVerificationStatus.VERIFIED, PartnerAvailability.ONLINE,
        PartnerWorkState.AVAILABLE, null, null, null, null, null,
        null, emptyList(), emptyList(), true, actions
    )

    private class FakeRepository(
        private val listResults: MutableList<AppResult<PagedDeliveryPartners>> = mutableListOf(),
        private val detailsResults: MutableList<AppResult<DeliveryPartnerDetails>> = mutableListOf()
    ) : DeliveryPartnerRepository {
        val queries = mutableListOf<DeliveryPartnersQuery>()
        val actions = mutableListOf<PartnerAdminAction>()
        override suspend fun getPartners(query: DeliveryPartnersQuery): AppResult<PagedDeliveryPartners> {
            queries += query
            return listResults.removeAt(0)
        }
        override suspend fun getPartnerDetails(partnerId: String): AppResult<DeliveryPartnerDetails> =
            detailsResults.removeAt(0)
        override suspend fun performAction(
            partnerId: String,
            action: PartnerAdminAction,
            reason: String?
        ): AppResult<Unit> {
            actions += action
            return AppResult.Success(Unit)
        }
    }
}
