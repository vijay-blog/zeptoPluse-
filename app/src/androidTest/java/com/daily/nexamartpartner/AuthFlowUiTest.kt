package com.daily.nexamartpartner

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.daily.nexamartpartner.core.result.AppFailure
import com.daily.nexamartpartner.core.result.AppResult
import com.daily.nexamartpartner.core.result.FailureType
import com.daily.nexamartpartner.di.appContainer
import com.daily.nexamartpartner.features.admin.domain.model.AdminDashboard
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderDetails
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderFilters
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSort
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrderSummary
import com.daily.nexamartpartner.features.admin.domain.model.AdminOrdersQuery
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryInfo
import com.daily.nexamartpartner.features.admin.domain.model.DashboardKpis
import com.daily.nexamartpartner.features.admin.domain.model.OrderCustomer
import com.daily.nexamartpartner.features.admin.domain.model.OrderItem
import com.daily.nexamartpartner.features.admin.domain.model.OrderStatus
import com.daily.nexamartpartner.features.admin.domain.model.OrderTimelineEntry
import com.daily.nexamartpartner.features.admin.domain.model.OrderTotals
import com.daily.nexamartpartner.features.admin.domain.model.PagedAdminOrders
import com.daily.nexamartpartner.features.admin.domain.model.PaymentInfo
import com.daily.nexamartpartner.features.admin.domain.model.PaymentMethod
import com.daily.nexamartpartner.features.admin.domain.model.PaymentStatus
import com.daily.nexamartpartner.features.admin.domain.model.RecentOrderSummary
import com.daily.nexamartpartner.features.admin.domain.repository.AdminDashboardRepository
import com.daily.nexamartpartner.features.admin.domain.repository.AdminOrdersRepository
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthFlowUiTest {
    private fun resetToUnauthenticated() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        runBlocking { container.sessionManager.clearSession() }
        container.authStateStore.setUnauthenticated()
        container.adminDashboardRepositoryOverride = null
        container.adminOrdersRepositoryOverride = null
    }

    @Test
    fun freshLaunchShowsLogin() {
        resetToUnauthenticated()
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(900)

        onView(withId(R.id.identifierInputEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordInputEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    @Test
    fun loginValidationShowsErrors() {
        resetToUnauthenticated()
        ActivityScenario.launch(MainActivity::class.java)
        Thread.sleep(900)

        onView(withId(R.id.loginButton)).perform(click())
        onView(withText("Please enter your phone or email.")).check(matches(isDisplayed()))
        onView(withText("Please enter your password.")).check(matches(isDisplayed()))
    }

    @Test
    fun roleRoutingNavigatesToExpectedPlaceholders() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        container.adminDashboardRepositoryOverride = FakeDashboardRepository(
            responses = mutableListOf(AppResult.Success(successDashboard()))
        )

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }
        onView(withText("NexaMart Admin")).check(matches(isDisplayed()))

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.DELIVERY_PARTNER))
        }
        onView(withText("NexaMart Delivery")).check(matches(isDisplayed()))

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setUnsupportedRole(
                "This account does not have permission to use the NexaMart Admin & Delivery application."
            )
        }
        onView(withText("Access Restricted")).check(matches(isDisplayed()))
    }

    @Test
    fun logoutReturnsToLogin() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        container.adminDashboardRepositoryOverride = FakeDashboardRepository(
            responses = mutableListOf(AppResult.Success(successDashboard()))
        )
        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }
        onView(withText("NexaMart Admin")).check(matches(isDisplayed()))
        onView(withId(R.id.logoutButton)).perform(click())
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    @Test
    fun adminDashboardRendersQuickActionsAndKpis() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        container.adminDashboardRepositoryOverride = FakeDashboardRepository(
            responses = mutableListOf(AppResult.Success(successDashboard()))
        )

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }

        onView(withId(R.id.adminDashboardTitle)).check(matches(isDisplayed()))
        onView(withText("Total Orders")).check(matches(isDisplayed()))
        onView(withText("Quick Actions")).check(matches(isDisplayed()))
        onView(withId(R.id.ordersActionButton)).check(matches(isDisplayed()))
        onView(withId(R.id.productsActionButton)).check(matches(isDisplayed()))
        onView(withId(R.id.categoriesActionButton)).check(matches(isDisplayed()))
        onView(withId(R.id.deliveryPartnersActionButton)).check(matches(isDisplayed()))
        onView(withId(R.id.customersActionButton)).check(matches(isDisplayed()))
    }

    @Test
    fun dashboardLoadingAndRetryStatesRender() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        container.adminDashboardRepositoryOverride = FakeDashboardRepository(
            responses = mutableListOf(
                AppResult.Failure(AppFailure("Service unavailable", 503, FailureType.SERVER)),
                AppResult.Success(successDashboard())
            ),
            delayMs = 900L
        )

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }

        onView(withId(R.id.dashboardLoadingSection)).check(matches(isDisplayed()))
        Thread.sleep(1200)
        onView(withText("Unable to load dashboard")).check(matches(isDisplayed()))
        onView(withId(R.id.retryButton)).perform(click())
        Thread.sleep(1200)
        onView(withText("Recent Orders")).check(matches(isDisplayed()))
    }

    @Test
    fun pullToRefreshRequestsDashboardAgain() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        val fakeRepository = FakeDashboardRepository(
            responses = mutableListOf(
                AppResult.Success(successDashboard()),
                AppResult.Success(successDashboard(totalOrders = 22))
            )
        )
        container.adminDashboardRepositoryOverride = fakeRepository

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }
        Thread.sleep(1000)

        onView(withId(R.id.dashboardSwipeRefresh)).perform(swipeDown())
        Thread.sleep(700)
        assertTrue(fakeRepository.callCount.get() >= 2)
    }

    @Test
    fun dashboardQuickActionNavigationWorks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        container.adminDashboardRepositoryOverride = FakeDashboardRepository(
            responses = mutableListOf(AppResult.Success(successDashboard()))
        )
        container.adminOrdersRepositoryOverride = FakeOrdersRepository()

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }
        Thread.sleep(1000)

        onView(withId(R.id.ordersActionButton)).perform(click())
        onView(withId(R.id.searchInputEditText)).check(matches(isDisplayed()))
        onView(withText("NM10234")).check(matches(isDisplayed()))
    }

    @Test
    fun ordersToDetailsAndBackWorks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer
        container.adminDashboardRepositoryOverride = FakeDashboardRepository(
            responses = mutableListOf(AppResult.Success(successDashboard()))
        )
        container.adminOrdersRepositoryOverride = FakeOrdersRepository()

        ActivityScenario.launch(MainActivity::class.java).onActivity {
            container.authStateStore.setAuthenticated(testSession(UserRole.ADMIN))
        }
        Thread.sleep(1000)

        onView(withId(R.id.ordersActionButton)).perform(click())
        onView(withText("NM10234")).perform(click())
        onView(withText("Order Details")).check(matches(isDisplayed()))
        onView(withId(R.id.backButton)).perform(click())
        onView(withId(R.id.ordersRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun deliveryPartnerCannotOpenAdminDashboardRoute() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer

        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            container.authStateStore.setAuthenticated(testSession(UserRole.DELIVERY_PARTNER))
            val navHost = activity.supportFragmentManager.findFragmentById(R.id.navHostFragment)
                    as androidx.navigation.fragment.NavHostFragment
            navHost.navController.navigate(R.id.adminDashboardFragment)
        }

        onView(withText("NexaMart Delivery")).check(matches(isDisplayed()))
    }

    @Test
    fun deliveryPartnerCannotOpenAdminOrdersRoute() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val container = context.appContainer

        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            container.authStateStore.setAuthenticated(testSession(UserRole.DELIVERY_PARTNER))
            val navHost = activity.supportFragmentManager.findFragmentById(R.id.navHostFragment)
                    as androidx.navigation.fragment.NavHostFragment
            navHost.navController.navigate(R.id.adminOrdersFragment)
        }

        onView(withText("NexaMart Delivery")).check(matches(isDisplayed()))
    }

    private fun testSession(role: UserRole): UserSession {
        return UserSession(
            accessToken = "access",
            refreshToken = "refresh",
            userId = 1L,
            name = "Test",
            contact = "9999999999",
            role = role
        )
    }

    private fun successDashboard(totalOrders: Long = 10L): AdminDashboard {
        return AdminDashboard(
            kpis = DashboardKpis(
                totalOrders = totalOrders,
                todayOrders = 2L,
                pendingOrders = 1L,
                outForDelivery = 3L,
                deliveredToday = 4L,
                todaySales = BigDecimal("1250.00"),
                currencyCode = "INR"
            ),
            recentOrders = listOf(
                RecentOrderSummary(
                    orderId = "#NM10025",
                    customerName = "Rahul",
                    amount = BigDecimal("850.00"),
                    currencyCode = "INR",
                    status = "OUT FOR DELIVERY",
                    createdAt = "2026-09-07T13:00:00Z"
                )
            ),
            recentOrdersAvailable = true
        )
    }

    private class FakeDashboardRepository(
        private val responses: MutableList<AppResult<AdminDashboard>>,
        private val delayMs: Long = 0L
    ) : AdminDashboardRepository {
        val callCount = AtomicInteger(0)

        override suspend fun getDashboard(): AppResult<AdminDashboard> {
            callCount.incrementAndGet()
            if (delayMs > 0) {
                delay(delayMs)
            }
            return if (responses.isNotEmpty()) {
                responses.removeAt(0)
            } else {
                AppResult.Success(
                    AdminDashboard(
                        kpis = null,
                        recentOrders = emptyList(),
                        recentOrdersAvailable = true
                    )
                )
            }
        }
    }

    private class FakeOrdersRepository : AdminOrdersRepository {
        override suspend fun getOrders(query: AdminOrdersQuery): AppResult<PagedAdminOrders> {
            return AppResult.Success(
                PagedAdminOrders(
                    orders = listOf(
                        AdminOrderSummary(
                            orderId = "NM10234",
                            customerName = "Rahul",
                            customerPhone = "9999999999",
                            itemCount = 2,
                            totalAmount = BigDecimal("850.00"),
                            currencyCode = "INR",
                            orderStatus = OrderStatus.OUT_FOR_DELIVERY,
                            paymentStatus = PaymentStatus.PENDING,
                            deliveryStatus = "OUT_FOR_DELIVERY",
                            createdAt = "2026-09-07T13:00:00Z"
                        )
                    ),
                    page = query.page,
                    pageSize = query.pageSize,
                    totalPages = 1,
                    totalElements = 1,
                    hasNextPage = false
                )
            )
        }

        override suspend fun getOrderDetails(orderId: String): AppResult<AdminOrderDetails> {
            return AppResult.Success(
                AdminOrderDetails(
                    orderId = orderId,
                    createdAt = "2026-09-07T13:00:00Z",
                    currentStatus = OrderStatus.OUT_FOR_DELIVERY,
                    customer = OrderCustomer("Rahul", "9999999999", "Chennai"),
                    items = listOf(
                        OrderItem(
                            productName = "Product A",
                            quantity = 2,
                            unitPrice = BigDecimal("425.00"),
                            lineTotal = BigDecimal("850.00"),
                            currencyCode = "INR"
                        )
                    ),
                    payment = PaymentInfo(PaymentMethod.COD, PaymentStatus.PENDING, null),
                    totals = OrderTotals(
                        subtotal = BigDecimal("800.00"),
                        deliveryFee = BigDecimal("50.00"),
                        discount = BigDecimal("0.00"),
                        tax = BigDecimal("0.00"),
                        grandTotal = BigDecimal("850.00"),
                        currencyCode = "INR"
                    ),
                    delivery = DeliveryInfo("OUT_FOR_DELIVERY", "Partner 1", "2026-09-07T13:15:00Z"),
                    timeline = listOf(
                        OrderTimelineEntry(OrderStatus.PENDING, null),
                        OrderTimelineEntry(OrderStatus.OUT_FOR_DELIVERY, null)
                    ),
                    allowedTransitions = listOf(OrderStatus.DELIVERED),
                    canCancel = false
                )
            )
        }

        override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): AppResult<Unit> {
            return AppResult.Success(Unit)
        }

        override suspend fun cancelOrder(orderId: String, reason: String?): AppResult<Unit> {
            return AppResult.Success(Unit)
        }
    }
}
