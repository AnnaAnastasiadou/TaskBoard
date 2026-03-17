package com.example.taskboard.data.preferences

interface SessionManager {
    fun setToken(token: String)
    fun getToken(): String?
    fun saveUserId(userId: Int)
    fun getUserId(): Int
    fun clearData()
}