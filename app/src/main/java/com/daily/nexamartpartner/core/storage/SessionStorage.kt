package com.daily.nexamartpartner.core.storage

import com.daily.nexamartpartner.features.auth.domain.model.UserSession

interface SessionStorage {
    fun saveSession(session: UserSession)
    fun readSession(): UserSession?
    fun clearSession()
}
