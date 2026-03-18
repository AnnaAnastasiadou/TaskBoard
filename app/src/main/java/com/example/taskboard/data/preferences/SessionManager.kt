package com.example.taskboard.data.preferences

interface SessionManager {
    fun setTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
}