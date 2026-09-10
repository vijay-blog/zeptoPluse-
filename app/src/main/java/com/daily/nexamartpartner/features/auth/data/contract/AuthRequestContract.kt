package com.daily.nexamartpartner.features.auth.data.contract

import com.daily.nexamartpartner.features.auth.domain.model.LoginCredentials

interface AuthRequestContract {
    fun buildLoginBody(credentials: LoginCredentials): Map<String, String>?
    fun buildRefreshBody(refreshToken: String): Map<String, String>?
}
