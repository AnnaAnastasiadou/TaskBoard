package com.example.taskboard.core

interface TokenProvider {
    fun setAccessToken(accessToken: String)
    fun getAccessToken(): String?
    fun clearAccessToken()
}