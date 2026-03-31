package com.example.taskboard.data.repository

import com.example.taskboard.core.TokenProvider
import com.example.taskboard.data.local.database.TaskBoardDatabase
import com.example.taskboard.data.remote.api.AuthApi
import com.example.taskboard.data.remote.dto.LoginRequest
import com.example.taskboard.data.remote.dto.LoginResponse
import com.example.taskboard.data.local.preferences.SharedPreferencesDatasource
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.dto.RefreshRequestDto
import com.example.taskboard.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val sharedPreferencesDatasource: SharedPreferencesDatasource,
    private val tokenProvider: TokenProvider,
    private val database: TaskBoardDatabase
) : AuthRepository {
    private val _isLoggedIn = MutableStateFlow(isUserLoggedIn())
    override val isLoggedIn = _isLoggedIn.asStateFlow()

    override fun refreshAccessTokenSync(): String? {
        val refreshToken = sharedPreferencesDatasource.getRefreshToken() ?: return null

        return try {
            val response = authApi.refreshToken(RefreshRequestDto(refreshToken)).execute()

            if (response.isSuccessful && response.body() != null) {
                val newTokens = response.body()!!

                sharedPreferencesDatasource.setTokens(newTokens.accessToken, newTokens.refreshToken)
                tokenProvider.setAccessToken(newTokens.accessToken)
                newTokens.accessToken
            } else {
                null
            }

        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logIn(
        username: String, password: String
    ): NetworkResult<LoginResponse> = withContext(Dispatchers.IO) {
        val result = safeCall { authApi.login(request = LoginRequest(username, password)) }

        if (result is NetworkResult.Success) {
            val loginData = result.data
            sharedPreferencesDatasource.setTokens(loginData.accessToken, loginData.refreshToken)
            tokenProvider.setAccessToken(loginData.accessToken)
            _isLoggedIn.value = true
        }
        result
    }


    override suspend fun logout() = withContext(Dispatchers.IO) {
        sharedPreferencesDatasource.clearTokens()
        tokenProvider.clearAccessToken()
        database.clearAllTables()
        _isLoggedIn.value = false
    }

    override fun isUserLoggedIn(): Boolean {
        val cachedToken = tokenProvider.getAccessToken()
        if (cachedToken != null) return true

        val persistedAccessToken = sharedPreferencesDatasource.getAccessToken()
        if (persistedAccessToken != null) {
            tokenProvider.setAccessToken(persistedAccessToken)
            return true
        }

        // If refresh token expires then
        return sharedPreferencesDatasource.getRefreshToken() != null
    }

}