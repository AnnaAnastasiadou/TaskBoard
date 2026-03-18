package com.example.taskboard.domain.repository

import com.example.taskboard.data.dto.LoginResponse
import com.example.taskboard.data.remote.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>
    suspend fun login(username: String, password: String) : NetworkResult<LoginResponse>
    suspend fun logout()
    fun isUserLoggedIn(): Boolean
}