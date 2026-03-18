package com.example.taskboard.core

import javax.inject.Inject

class TokenProviderImpl @Inject constructor() : TokenProvider {
    private var cachedToken: String? = null

    override fun setAccessToken(accessToken: String) {
        cachedToken = accessToken
    }

    override fun getAccessToken(): String? = cachedToken

    override fun clearAccessToken() {
        cachedToken = null
    }
}