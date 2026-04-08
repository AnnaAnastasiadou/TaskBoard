package com.example.taskboard.data.local.preferences

interface SharedPreferencesDatasource {
    fun setTokens(accessToken: String, refreshToken: String)
    fun setUserId(userId: Int)
    fun getUserId(): Int?
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clear()
}