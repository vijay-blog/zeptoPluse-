package com.daily.nexamartpartner.features.auth.data.contract

import com.daily.nexamartpartner.features.auth.domain.model.RegistrationCredentials

class RegistrationRequestContract {
    fun buildBody(credentials: RegistrationCredentials): Map<String, String> = mapOf(
        "name" to credentials.name.trim(),
        "email" to credentials.email.trim().lowercase(),
        "password" to credentials.password
    )
}
