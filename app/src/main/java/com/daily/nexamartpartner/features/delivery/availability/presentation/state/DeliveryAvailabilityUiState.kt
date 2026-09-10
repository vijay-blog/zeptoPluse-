package com.daily.nexamartpartner.features.delivery.availability.presentation.state

import com.daily.nexamartpartner.features.delivery.availability.domain.model.DeliveryAvailability

data class DeliveryAvailabilityUiState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val saving: Boolean = false,
    val availability: DeliveryAvailability? = null,
    val error: String? = null,
    val unavailable: Boolean = false
)
