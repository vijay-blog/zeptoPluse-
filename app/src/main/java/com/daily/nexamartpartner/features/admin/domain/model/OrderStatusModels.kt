package com.daily.nexamartpartner.features.admin.domain.model

enum class OrderStatus(val backendValue: String) {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    PREPARING("PREPARING"),
    READY("READY"),
    ASSIGNED("ASSIGNED"),
    ACCEPTED("ACCEPTED"),
    PICKED_UP("PICKED_UP"),
    OUT_FOR_DELIVERY("OUT_FOR_DELIVERY"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun fromRaw(raw: String?): OrderStatus {
            if (raw.isNullOrBlank()) return UNKNOWN
            return entries.firstOrNull { it.backendValue.equals(raw.trim(), ignoreCase = true) }
                ?: UNKNOWN
        }
    }
}

enum class PaymentStatus(val backendValue: String) {
    PENDING("PENDING"),
    PAID("PAID"),
    FAILED("FAILED"),
    REFUNDED("REFUNDED"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun fromRaw(raw: String?): PaymentStatus {
            if (raw.isNullOrBlank()) return UNKNOWN
            return entries.firstOrNull { it.backendValue.equals(raw.trim(), ignoreCase = true) }
                ?: UNKNOWN
        }
    }
}

enum class PaymentMethod(val backendValue: String) {
    COD("COD"),
    UPI("UPI"),
    CARD("CARD"),
    ONLINE("ONLINE"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun fromRaw(raw: String?): PaymentMethod {
            if (raw.isNullOrBlank()) return UNKNOWN
            return entries.firstOrNull { it.backendValue.equals(raw.trim(), ignoreCase = true) }
                ?: UNKNOWN
        }
    }
}
