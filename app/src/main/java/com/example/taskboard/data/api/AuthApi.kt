package com.example.taskboard.data.api

import com.example.taskboard.data.dto.LoginRequest
import com.example.taskboard.data.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest) : Response<LoginResponse>
}