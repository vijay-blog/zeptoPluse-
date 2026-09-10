package com.daily.nexamartpartner.core.result

enum class FailureType {
    NETWORK,
    UNAUTHORIZED,
    FORBIDDEN,
    VALIDATION,
    CONTRACT_MISSING,
    UNSUPPORTED_ROLE,
    SERVER,
    NOT_FOUND,
    CONFLICT,
    TRANSIENT,
    UNKNOWN
}

data class AppFailure(
    val message: String,
    val code: Int? = null,
    val type: FailureType = FailureType.UNKNOWN
)
