package com.example.taskboard.data.local.preferences

interface SharedPreferencesDatasource {
    fun setTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
}