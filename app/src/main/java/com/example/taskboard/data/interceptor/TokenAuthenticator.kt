package com.example.taskboard.data.interceptor

import com.example.taskboard.core.TokenProvider
import com.example.taskboard.data.api.AuthApi
import com.example.taskboard.data.dto.RefreshRequestDto
import com.example.taskboard.data.preferences.SessionManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val sessionManager: SessionManager,
    private val authApi: Provider<AuthApi>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = sessionManager.getRefreshToken()

        if (refreshToken != null) {
            // Call the refresh API synchronously
            // Note: .get() is used because of the Provider wrapper
            val refreshResponse =
                authApi.get().refreshToken(RefreshRequestDto((refreshToken))).execute()
            if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newTokens = refreshResponse.body()!!

                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}").build()
            }
        }
        return null
    }

}