package com.daily.nexamartpartner.features.auth.domain.model

data class UserSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val name: String,
    val contact: String?,
    val role: UserRole
)
