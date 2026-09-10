package com.daily.nexamartpartner.features.delivery.data.contract

interface DeliveryDashboardContract {
    val dashboardPath: String?
}

/**
 * Keeps delivery dashboard integration disabled until the Spring Boot backend
 * confirms the exact production endpoint and response contract.
 */
class PendingBackendDeliveryDashboardContract : DeliveryDashboardContract {
    override val dashboardPath: String? = null
}
