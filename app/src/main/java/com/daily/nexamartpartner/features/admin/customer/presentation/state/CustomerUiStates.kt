package com.daily.nexamartpartner.features.admin.customer.presentation.state

import com.daily.nexamartpartner.features.admin.customer.domain.model.Customer

sealed interface CustomerListContent {
    data object Loading : CustomerListContent
    data class Success(val items: List<Customer>, val hasNextPage: Boolean) : CustomerListContent
    data class Empty(val message: String) : CustomerListContent
    data class Error(val message: String) : CustomerListContent
    data class Unavailable(val message: String) : CustomerListContent
}

data class CustomerListUiState(
    val searchQuery: String = "",
    val status: String? = null,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val content: CustomerListContent = CustomerListContent.Loading
)

sealed interface CustomerDetailsContent {
    data object Loading : CustomerDetailsContent
    data class Success(val customer: Customer) : CustomerDetailsContent
    data class Error(val message: String) : CustomerDetailsContent
    data class Unavailable(val message: String) : CustomerDetailsContent
}

data class CustomerDetailsUiState(
    val isRefreshing: Boolean = false,
    val actionInProgress: String? = null,
    val content: CustomerDetailsContent = CustomerDetailsContent.Loading
)
