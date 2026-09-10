package com.daily.nexamartpartner.features.delivery.notifications.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daily.nexamartpartner.features.delivery.notifications.domain.usecase.*
class DeliveryNotificationsViewModelFactory(
    private val get: GetDeliveryNotificationsUseCase,
    private val read: MarkDeliveryNotificationReadUseCase,
    private val readAll: MarkAllDeliveryNotificationsReadUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(DeliveryNotificationsViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return DeliveryNotificationsViewModel(get, read, readAll) as T
    }
}
