package com.daily.nexamartpartner.features.auth.domain.model

enum class UserRole {
    ADMIN,
    DELIVERY_PARTNER,
    UNSUPPORTED;

    companion object {
        fun fromRaw(value: String?): UserRole {
            return when (value?.trim()?.uppercase()) {
                "ADMIN" -> ADMIN
                "DELIVERY_PARTNER" -> DELIVERY_PARTNER
                else -> UNSUPPORTED
            }
        }
    }
}
