package com.daily.nexamartpartner.features.auth.data.model

import com.squareup.moshi.Json

data class AuthUserDto(
    @field:Json(name = "id") val id: Long?,
    @field:Json(name = "name") val name: String?,
    @field:Json(name = "phone") val phone: String?,
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "role") val role: String?
)

data class LoginResponseDto(
    @field:Json(name = "accessToken") val accessToken: String?,
    @field:Json(name = "refreshToken") val refreshToken: String?,
    @field:Json(name = "user") val user: AuthUserDto?
)
