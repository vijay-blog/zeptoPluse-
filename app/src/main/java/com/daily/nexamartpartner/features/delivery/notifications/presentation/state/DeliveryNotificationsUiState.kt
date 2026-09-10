package com.daily.nexamartpartner.features.delivery.notifications.presentation.state
import com.daily.nexamartpartner.features.delivery.notifications.domain.model.DeliveryNotification
sealed interface NotificationContent{data object Loading:NotificationContent;data class Success(val items:List<DeliveryNotification>,val hasNext:Boolean):NotificationContent;data class Empty(val title:String,val message:String):NotificationContent;data class Error(val title:String,val message:String):NotificationContent;data class Unavailable(val title:String,val message:String):NotificationContent}
data class DeliveryNotificationsUiState(val unreadCount:Int=0,val isRefreshing:Boolean=false,val isLoadingMore:Boolean=false,val content:NotificationContent=NotificationContent.Loading)
