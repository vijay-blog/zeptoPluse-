package com.daily.nexamartpartner.features.auth.data.contract

import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials

class PendingBackendAuthRequestContract : AuthRequestContract {
    override fun buildLoginBody(credentials: LoginCredentials): Map<String, String>? = null

    override fun buildRefreshBody(refreshToken: String): Map<String, String>? = null
}
