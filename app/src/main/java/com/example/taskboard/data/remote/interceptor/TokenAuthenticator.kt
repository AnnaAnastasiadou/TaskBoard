package com.example.taskboard.data.remote.interceptor

import com.example.taskboard.core.TokenProvider
import com.example.taskboard.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    // OkHttp needs TokenAuthenticator and vice versa so we use Provider to stop circular dependency
    private val authRepository: Provider<AuthRepository>,
    private val tokenProvider: TokenProvider
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. Manually check the retry count to avoid infinite loops
        if (response.retryCount() >= 3) {
            return null
        }

        synchronized(this) {
            val repo = authRepository.get()

            // Get the current local token vs the one used in the failed request
            val currentToken = tokenProvider.getAccessToken()
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            val tokenToUse = if (currentToken != requestToken) {
                // If they differ, another thread already refreshed it!
                currentToken
            } else {
                // They are the same, meaning the token is truly expired.
                // Call your blocking refresh method here.
                repo.refreshAccessTokenSync()
            }

            return if (tokenToUse != null) {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $tokenToUse")
                    .build()
            } else {
                // If refresh failed (e.g. Refresh Token expired), log out.
                runBlocking { repo.logout() }
                null
            }
        }
    }

    // Helper to count how many times this specific request has been retried
    private fun Response.retryCount(): Int {
        var count = 0
        var res: Response? = this
        while (res != null) {
            count++
            res = res.priorResponse
        }
        return count
    }

}