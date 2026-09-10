package com.daily.nexamartpartner.features.delivery.presentation.state

import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrderSummary

data class DeliveryHistoryFilters(
    val search: String = "",
    val status: String? = null,
    val fromDate: String? = null,
    val toDate: String? = null
)

data class DeliveryHistoryUiState(
    val filters: DeliveryHistoryFilters = DeliveryHistoryFilters(),
    val isRefreshing:Boolean=false,
    val isLoadingMore:Boolean=false,
    val content:Content=Content.Loading
){
 sealed interface Content { data object Loading:Content; data class Success(val orders:List<DeliveryOrderSummary>,val hasNext:Boolean):Content; data class Empty(val title:String,val message:String):Content; data class Error(val title:String,val message:String):Content; data class Unavailable(val title:String,val message:String):Content }
}
