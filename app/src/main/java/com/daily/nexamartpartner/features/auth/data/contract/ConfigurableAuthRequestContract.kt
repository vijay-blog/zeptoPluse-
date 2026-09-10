package com.daily.nexamartpartner.features.auth.data.contract

import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials

class ConfigurableAuthRequestContract(
    private val identifierField: String = "email",
    private val passwordField: String = "password",
    private val refreshTokenField: String = "refreshToken"
) : AuthRequestContract {
    override fun buildLoginBody(credentials: LoginCredentials): Map<String, String> = mapOf(
        identifierField to credentials.identifier.trim(),
        passwordField to credentials.password
    )

    override fun buildRefreshBody(refreshToken: String): Map<String, String> = mapOf(refreshTokenField to refreshToken)
}
