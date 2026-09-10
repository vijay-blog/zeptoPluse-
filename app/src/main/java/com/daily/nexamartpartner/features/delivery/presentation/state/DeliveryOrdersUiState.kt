package com.daily.nexamartpartner.features.delivery.presentation.state

import com.daily.nexamartpartner.features.delivery.domain.model.DeliveryOrderSummary

data class DeliveryOrdersUiState(val search:String="",val isRefreshing:Boolean=false,val isLoadingMore:Boolean=false,val content:Content=Content.Loading){
 sealed interface Content{data object Loading:Content;data class Success(val orders:List<DeliveryOrderSummary>,val hasNext:Boolean):Content;data class Empty(val title:String,val message:String):Content;data class Error(val title:String,val message:String):Content;data class Unavailable(val title:String,val message:String):Content}
}
