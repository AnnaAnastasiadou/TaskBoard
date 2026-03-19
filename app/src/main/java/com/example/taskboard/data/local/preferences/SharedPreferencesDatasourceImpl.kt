package com.example.taskboard.data.local.preferences

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class SharedPreferencesDatasourceImpl @Inject constructor(@ApplicationContext context: Context) :
    SharedPreferencesDatasource {
    private val preferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    override fun setTokens(accessToken: String, refreshToken: String) {
        preferences.edit().apply{
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    override fun getAccessToken(): String? {
        return preferences.getString(KEY_ACCESS_TOKEN, null)
    }
    override fun getRefreshToken(): String? {
        return preferences.getString(KEY_ACCESS_TOKEN, null)
    }

    override fun clearTokens() {
        preferences.edit { clear() }
    }

}