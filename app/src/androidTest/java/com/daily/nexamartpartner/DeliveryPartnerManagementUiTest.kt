package com.daily.nexamartpartner

import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.di.appContainer
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
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeliveryPartnerManagementUiTest {
    @Test
    fun adminCanOpenListAndPartnerDetails() {
        val container = InstrumentationRegistry.getInstrumentation().targetContext.appContainer
        container.deliveryPartnerRepositoryOverride = FakePartnerRepository()
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            container.authStateStore.setAuthenticated(session(UserRole.ADMIN))
            val host = activity.supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            host.navController.navigate(R.id.adminDeliveryPartnersFragment)
        }
        Thread.sleep(700)
        onView(withId(R.id.partnerSearchInput)).check(matches(isDisplayed()))
        onView(withId(R.id.partnerFilterButton)).check(matches(isDisplayed()))
        onView(withText("Test Partner")).perform(click())
        onView(withText("Delivery Partner Details")).check(matches(isDisplayed()))
        onView(withText("Account & Availability")).check(matches(isDisplayed()))
        onView(withText("Delivery Statistics")).check(matches(isDisplayed()))
        onView(withText("Current Orders")).check(matches(isDisplayed()))
    }

    @Test
    fun deliveryPartnerCannotOpenAdminPartnerManagement() {
        val container = InstrumentationRegistry.getInstrumentation().targetContext.appContainer
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            container.authStateStore.setAuthenticated(session(UserRole.DELIVERY_PARTNER))
            val host = activity.supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            host.navController.navigate(R.id.adminDeliveryPartnersFragment)
        }
        onView(withText("NexaMart Delivery")).check(matches(isDisplayed()))
    }

    private fun session(role: UserRole) = UserSession(
        "access", "refresh", 1, "Test", "9999999999", role
    )

    private class FakePartnerRepository : DeliveryPartnerRepository {
        override suspend fun getPartners(query: DeliveryPartnersQuery) = AppResult.Success(
            PagedDeliveryPartners(listOf(summary()), 0, 20, 1, 1, false)
        )

        override suspend fun getPartnerDetails(partnerId: String) = AppResult.Success(
            DeliveryPartnerDetails(
                partnerId, "Test Partner", "9999999999", "test@example.com", null,
                PartnerAccountStatus.ACTIVE, PartnerVerificationStatus.VERIFIED,
                PartnerAvailability.ONLINE, PartnerWorkState.AVAILABLE,
                "2026-09-01T10:00:00Z", "2026-09-09T07:00:00Z",
                "BIKE", "TN00AA0000", null,
                DeliveryPartnerStatistics(10, 9, 1, 1),
                listOf(PartnerOrderSummary("NM1001", OrderStatus.OUT_FOR_DELIVERY, null)),
                emptyList(), true, listOf(PartnerAdminAction.DEACTIVATE)
            )
        )

        override suspend fun performAction(
            partnerId: String,
            action: PartnerAdminAction,
            reason: String?
        ) = AppResult.Success(Unit)

        private fun summary() = DeliveryPartnerSummary(
            "DP1", "Test Partner", "9999999999", null,
            PartnerAccountStatus.ACTIVE, PartnerVerificationStatus.VERIFIED,
            PartnerAvailability.ONLINE, PartnerWorkState.AVAILABLE, 1, true
        )
    }
}
