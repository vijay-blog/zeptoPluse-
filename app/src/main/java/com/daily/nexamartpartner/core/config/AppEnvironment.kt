package com.daily.nexamartpartner.core.config

enum class AppEnvironment(val label: String) {
    DEVELOPMENT("Development"),
    STAGING("Staging"),
    PRODUCTION("Production");

    companion object {
        fun fromRaw(value: String): AppEnvironment {
            return when (value.lowercase()) {
                "development" -> DEVELOPMENT
                "staging" -> STAGING
                "production" -> PRODUCTION
                else -> DEVELOPMENT
            }
        }
    }
}
