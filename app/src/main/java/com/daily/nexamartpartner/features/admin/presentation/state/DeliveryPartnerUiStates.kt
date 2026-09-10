package com.daily.nexamartpartner.features.admin.presentation.state

import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerDetails
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerFilters
import com.daily.nexamartpartner.features.admin.domain.model.DeliveryPartnerSummary
import com.daily.nexamartpartner.features.admin.domain.model.PartnerAdminAction

data class DeliveryPartnerListUiState(
    val searchQuery: String = "",
    val filters: DeliveryPartnerFilters = DeliveryPartnerFilters(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val content: Content = Content.Loading
) {
    sealed interface Content {
        data object Loading : Content
        data class Success(val partners: List<DeliveryPartnerSummary>, val hasNextPage: Boolean) : Content
        data class Empty(val message: String, val showClearFilters: Boolean) : Content
        data class Error(val message: String) : Content
        data class Unavailable(val message: String) : Content
    }
}

data class DeliveryPartnerDetailsUiState(
    val isRefreshing: Boolean = false,
    val actionInProgress: PartnerAdminAction? = null,
    val content: Content = Content.Loading
) {
    sealed interface Content {
        data object Loading : Content
        data class Success(val partner: DeliveryPartnerDetails) : Content
        data class Error(val message: String) : Content
        data class Unavailable(val message: String) : Content
    }
}
