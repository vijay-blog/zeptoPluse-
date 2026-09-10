package com.daily.nexamartpartner.routing

import com.daily.nexamartpartner.R

enum class DestinationScope {
    PUBLIC,
    ADMIN,
    DELIVERY
}

enum class AppDestination(val navId: Int, val scope: DestinationScope) {
    SPLASH(R.id.splashFragment, DestinationScope.PUBLIC),
    LOGIN(R.id.loginFragment, DestinationScope.PUBLIC),
    ACCESS_DENIED(R.id.unsupportedRoleFragment, DestinationScope.PUBLIC),

    ADMIN_GRAPH(R.id.adminGraph, DestinationScope.ADMIN),
    ADMIN_DASHBOARD(R.id.adminDashboardFragment, DestinationScope.ADMIN),
    ADMIN_ORDERS(R.id.adminOrdersFragment, DestinationScope.ADMIN),
    ADMIN_ORDER_DETAILS(R.id.adminOrderDetailsFragment, DestinationScope.ADMIN),
    ADMIN_PRODUCTS(R.id.adminProductsFragment, DestinationScope.ADMIN),
    ADMIN_PRODUCT_DETAILS(R.id.adminProductDetailsFragment, DestinationScope.ADMIN),
    ADMIN_PRODUCT_FORM(R.id.adminProductFormFragment, DestinationScope.ADMIN),
    ADMIN_CATEGORIES(R.id.adminCategoriesFragment, DestinationScope.ADMIN),
    ADMIN_CUSTOMERS(R.id.adminCustomersFragment, DestinationScope.ADMIN),
    ADMIN_DELIVERY_PARTNERS(R.id.adminDeliveryPartnersFragment, DestinationScope.ADMIN),
    ADMIN_DELIVERY_PARTNER_DETAILS(R.id.adminDeliveryPartnerDetailsFragment, DestinationScope.ADMIN),
    ADMIN_REPORTS(R.id.adminReportsPlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_NOTIFICATIONS(R.id.adminNotificationsPlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_PROFILE(R.id.adminProfilePlaceholderFragment, DestinationScope.ADMIN),
    ADMIN_SETTINGS(R.id.adminSettingsFragment, DestinationScope.ADMIN),

    DELIVERY_GRAPH(R.id.deliveryGraph, DestinationScope.DELIVERY),
    DELIVERY_DASHBOARD(R.id.deliveryDashboardFragment, DestinationScope.DELIVERY),
    DELIVERY_ASSIGNED_ORDERS(R.id.deliveryAssignedOrdersFragment, DestinationScope.DELIVERY),
    DELIVERY_ORDER_DETAILS(R.id.deliveryOrderDetailsFragment, DestinationScope.DELIVERY),
    DELIVERY_PICKUP(R.id.deliveryPickupPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_RUN(R.id.deliveryRunPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_HISTORY(R.id.deliveryHistoryFragment, DestinationScope.DELIVERY),
    DELIVERY_EARNINGS(R.id.deliveryEarningsPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_PROFILE(R.id.deliveryProfilePlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_AVAILABILITY(R.id.deliveryAvailabilityPlaceholderFragment, DestinationScope.DELIVERY),
    DELIVERY_NOTIFICATIONS(R.id.deliveryNotificationsPlaceholderFragment, DestinationScope.DELIVERY);

    companion object {
        fun fromNavId(navId: Int): AppDestination? = entries.firstOrNull { it.navId == navId }
    }
}
