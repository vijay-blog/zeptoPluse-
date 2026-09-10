package com.daily.nexamartpartner.features.auth.domain.session

import com.daily.nexamartpartner.core.storage.SessionStorage
import com.daily.nexamartpartner.features.auth.domain.model.SessionStatus
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SessionManager(private val storage: SessionStorage) {
    private val lock = Mutex()

    private val _sessionStatus = MutableStateFlow(SessionStatus.UNKNOWN)
    val sessionStatus: StateFlow<SessionStatus> = _sessionStatus.asStateFlow()

    private val _currentSession = MutableStateFlow<UserSession?>(null)
    val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

    suspend fun initialize(): UserSession? = lock.withLock {
        val restored = storage.readSession()
        _currentSession.value = restored
        _sessionStatus.value = if (restored == null) {
            SessionStatus.UNAUTHENTICATED
        } else {
            SessionStatus.AUTHENTICATED
        }
        restored
    }

    suspend fun saveSession(session: UserSession) = lock.withLock {
        storage.saveSession(session)
        _currentSession.value = session
        _sessionStatus.value = SessionStatus.AUTHENTICATED
    }

    suspend fun clearSession() = lock.withLock {
        storage.clearSession()
        _currentSession.value = null
        _sessionStatus.value = SessionStatus.UNAUTHENTICATED
    }

    fun hasSession(): Boolean = _currentSession.value != null

    fun getCurrentRole(): UserRole? = _currentSession.value?.role
}
