package com.example.taskboard.domain.repository

import com.example.taskboard.data.remote.dto.LoginResponse
import com.example.taskboard.data.remote.NetworkResult
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>
    fun refreshAccessTokenSync(): String?
    suspend fun logIn(username: String, password: String) : NetworkResult<LoginResponse>
    suspend fun logout()
    fun isUserLoggedIn(): Boolean
}