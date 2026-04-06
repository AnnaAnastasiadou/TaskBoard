package com.example.taskboard.core

interface SessionProvider {
    fun setAccessToken(accessToken: String)
    fun getAccessToken(): String?
    fun setUserId(userId: Int)
    fun getUserId(): Int?
    fun clearAccessToken()
}