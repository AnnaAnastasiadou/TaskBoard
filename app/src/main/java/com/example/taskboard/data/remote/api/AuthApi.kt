package com.example.taskboard.data.remote.api

import com.example.taskboard.data.remote.dto.LoginRequest
import com.example.taskboard.data.remote.dto.LoginResponse
import com.example.taskboard.data.remote.dto.RefreshRequestDto
import com.example.taskboard.data.remote.dto.RefreshResponseDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest) : Response<LoginResponse>

    @POST("auth/refresh")
    fun refreshToken(@Body request: RefreshRequestDto): Call<RefreshResponseDto>
}