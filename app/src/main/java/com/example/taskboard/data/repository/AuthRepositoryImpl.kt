package com.example.taskboard.data.repository

import com.example.taskboard.core.TokenProvider
import com.example.taskboard.data.api.AuthApi
import com.example.taskboard.data.dto.LoginRequest
import com.example.taskboard.data.dto.LoginResponse
import com.example.taskboard.data.preferences.SessionManager
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
    private val tokenProvider: TokenProvider
) : AuthRepository {
    private val _isLoggedIn = MutableStateFlow(isUserLoggedIn())
    override val isLoggedIn = _isLoggedIn.asStateFlow()

    override suspend fun logIn(username: String, password: String): NetworkResult<LoginResponse> =
        withContext(Dispatchers.IO) {
            val result = safeCall { authApi.login(request = LoginRequest(username, password)) }

            if (result is NetworkResult.Success) {
                val loginData = result.data
                sessionManager.setTokens(loginData.accessToken, loginData.refreshToken)
                tokenProvider.setAccessToken(loginData.accessToken)
                _isLoggedIn.value = true
            }
            result
        }


    override suspend fun logout() = withContext(Dispatchers.IO) {
        sessionManager.clearTokens()
        tokenProvider.clearAccessToken()
        _isLoggedIn.value = false
    }

    override fun isUserLoggedIn(): Boolean {
        var token = tokenProvider.getAccessToken()
        if (token == null) {
            sessionManager.getAccessToken()?.let {
                token = it
                tokenProvider.setAccessToken(token)
            }
        }
        return token != null
    }

}