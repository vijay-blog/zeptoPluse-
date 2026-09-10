package com.daily.nexamartpartner.testutil

import com.daily.nexamartpartner.core.storage.SessionStorage
import com.daily.nexamartpartner.features.auth.domain.model.UserSession

class InMemorySessionStorage : SessionStorage {
    var session: UserSession? = null

    override fun saveSession(session: UserSession) {
        this.session = session
    }

    override fun readSession(): UserSession? = session

    override fun clearSession() {
        session = null
    }
}
