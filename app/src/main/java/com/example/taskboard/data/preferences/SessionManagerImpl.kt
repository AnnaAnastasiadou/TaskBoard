package com.example.taskboard.data.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManagerImpl @Inject constructor(@ApplicationContext context: Context) :
    SessionManager {
    private val preferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
    }

    override fun setToken(token: String) {
        preferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    override fun getToken(): String? {
        return preferences.getString(KEY_ACCESS_TOKEN, null)
    }

    override fun saveUserId(userId: Int) {
        preferences.edit().putInt(KEY_USER_ID, userId).apply()
    }

    override fun getUserId(): Int {
        return preferences.getInt(KEY_USER_ID, -1)
    }

    override fun clearData() {
        preferences.edit().clear().apply()
    }

}