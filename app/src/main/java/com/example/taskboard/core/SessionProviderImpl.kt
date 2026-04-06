package com.example.taskboard.core

import javax.inject.Inject

class SessionProviderImpl @Inject constructor() : SessionProvider {
    private var cachedToken: String? = null
    private var cachedUser: Int? = null

    override fun setAccessToken(accessToken: String) {
        cachedToken = accessToken
    }

    override fun getAccessToken(): String? = cachedToken

    override fun setUserId(userId: Int) {
        cachedUser = userId
    }
    override fun getUserId(): Int? = cachedUser

    override fun clearAccessToken() {
        cachedToken = null
    }
}