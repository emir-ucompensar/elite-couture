package com.elitecouture.app.data.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Simple session tracker. Not suitable for production but enough for an academic demo.
 */
class SessionManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setActiveUserId(userId: Long) {
        preferences.edit {
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
    }

    fun setGuestModeEnabled(isGuest: Boolean) {
        preferences.edit {
            putBoolean(KEY_IS_GUEST, isGuest)
            putBoolean(KEY_IS_LOGGED_IN, !isGuest && preferences.getLong(KEY_USER_ID, -1L) != -1L)
        }
    }

    fun clearSession() {
        preferences.edit { clear() }
    }

    fun isGuest(): Boolean = preferences.getBoolean(KEY_IS_GUEST, false)

    fun isLoggedIn(): Boolean = preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun currentUserId(): Long = preferences.getLong(KEY_USER_ID, -1L)

    companion object {
        private const val PREFS_NAME = "elite_couture_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_GUEST = "is_guest"
    }
}
