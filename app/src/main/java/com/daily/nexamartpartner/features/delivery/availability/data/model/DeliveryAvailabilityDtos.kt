package com.daily.nexamartpartner.features.delivery.availability.data.model

data class DeliveryAvailabilityDto(
    val available: Boolean? = null,
    val status: String? = null,
    val canChange: Boolean? = null,
    val reason: String? = null,
    val updatedAt: String? = null
)
