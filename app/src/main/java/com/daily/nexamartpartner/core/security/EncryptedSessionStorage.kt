package com.daily.nexamartpartner.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.daily.nexamartpartner.core.storage.SessionStorage
import com.daily.nexamartpartner.features.auth.domain.model.UserRole
import com.daily.nexamartpartner.features.auth.domain.model.UserSession

class EncryptedSessionStorage(context: Context) : SessionStorage {
    private val preferences = EncryptedSharedPreferences.create(
        context,
        STORAGE_FILE,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveSession(session: UserSession) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .putString(KEY_REFRESH_TOKEN, session.refreshToken)
            .putLong(KEY_USER_ID, session.userId)
            .putString(KEY_NAME, session.name)
            .putString(KEY_CONTACT, session.contact)
            .putString(KEY_ROLE, session.role.name)
            .apply()
    }

    override fun readSession(): UserSession? {
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null) ?: return null
        val refreshToken = preferences.getString(KEY_REFRESH_TOKEN, null) ?: return null
        val userId = preferences.getLong(KEY_USER_ID, -1L)
        val name = preferences.getString(KEY_NAME, null).orEmpty()
        val contact = preferences.getString(KEY_CONTACT, null)
        val role = UserRole.fromRaw(preferences.getString(KEY_ROLE, null))

        if (userId < 0L) return null
        if (role == UserRole.UNSUPPORTED) return null

        return UserSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            name = name,
            contact = contact,
            role = role
        )
    }

    override fun clearSession() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val STORAGE_FILE = "nexamart_secure_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_NAME = "name"
        private const val KEY_CONTACT = "contact"
        private const val KEY_ROLE = "role"
    }
}
