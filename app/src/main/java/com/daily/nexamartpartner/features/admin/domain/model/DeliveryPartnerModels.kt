package com.daily.nexamartpartner.features.admin.domain.model

enum class PartnerAccountStatus(val backendValue: String) {
    PENDING("PENDING"), ACTIVE("ACTIVE"), INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED"), REJECTED("REJECTED"), UNKNOWN("UNKNOWN");

    companion object {
        fun fromRaw(raw: String?): PartnerAccountStatus =
            entries.firstOrNull { it.backendValue.equals(raw?.trim(), true) } ?: UNKNOWN
    }
}

enum class PartnerVerificationStatus(val backendValue: String) {
    PENDING("PENDING"), VERIFIED("VERIFIED"), REJECTED("REJECTED"), UNKNOWN("UNKNOWN");

    companion object {
        fun fromRaw(raw: String?): PartnerVerificationStatus =
            entries.firstOrNull { it.backendValue.equals(raw?.trim(), true) } ?: UNKNOWN
    }
}

enum class PartnerAvailability(val backendValue: String) {
    ONLINE("ONLINE"), OFFLINE("OFFLINE"), UNKNOWN("UNKNOWN");

    companion object {
        fun fromRaw(raw: String?): PartnerAvailability =
            entries.firstOrNull { it.backendValue.equals(raw?.trim(), true) } ?: UNKNOWN
    }
}

enum class PartnerWorkState(val backendValue: String) {
    AVAILABLE("AVAILABLE"), BUSY("BUSY"), UNKNOWN("UNKNOWN");

    companion object {
        fun fromRaw(raw: String?): PartnerWorkState =
            entries.firstOrNull { it.backendValue.equals(raw?.trim(), true) } ?: UNKNOWN
    }
}

enum class PartnerAdminAction(val backendValue: String) {
    VERIFY("VERIFY"), REJECT("REJECT"), ACTIVATE("ACTIVATE"), DEACTIVATE("DEACTIVATE"),
    SUSPEND("SUSPEND"), REACTIVATE("REACTIVATE")
}

data class DeliveryPartnerSummary(
    val partnerId: String,
    val name: String,
    val phone: String?,
    val profileImageUrl: String?,
    val accountStatus: PartnerAccountStatus,
    val verificationStatus: PartnerVerificationStatus,
    val availability: PartnerAvailability,
    val workState: PartnerWorkState,
    val activeDeliveries: Int?,
    val isAssignable: Boolean?
)

data class PagedDeliveryPartners(
    val partners: List<DeliveryPartnerSummary>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNextPage: Boolean
)

data class DeliveryPartnerFilters(
    val accountStatus: PartnerAccountStatus? = null,
    val verificationStatus: PartnerVerificationStatus? = null,
    val availability: PartnerAvailability? = null
)

data class DeliveryPartnersQuery(
    val page: Int,
    val pageSize: Int,
    val searchText: String?,
    val filters: DeliveryPartnerFilters
)

data class DeliveryPartnerDetails(
    val partnerId: String,
    val name: String,
    val phone: String?,
    val email: String?,
    val profileImageUrl: String?,
    val accountStatus: PartnerAccountStatus,
    val verificationStatus: PartnerVerificationStatus,
    val availability: PartnerAvailability,
    val workState: PartnerWorkState,
    val registeredAt: String?,
    val lastActiveAt: String?,
    val vehicleType: String?,
    val vehicleNumber: String?,
    val licenseReference: String?,
    val statistics: DeliveryPartnerStatistics?,
    val currentOrders: List<PartnerOrderSummary>,
    val recentHistory: List<PartnerOrderSummary>,
    val isAssignable: Boolean?,
    val allowedActions: List<PartnerAdminAction>
)

data class DeliveryPartnerStatistics(
    val totalDeliveries: Long?,
    val completedDeliveries: Long?,
    val cancelledDeliveries: Long?,
    val activeDeliveries: Long?
)

data class PartnerOrderSummary(
    val orderId: String,
    val status: OrderStatus,
    val timestamp: String?
)
