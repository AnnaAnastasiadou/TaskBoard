package com.example.taskboard.data.api

import com.example.taskboard.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserDto>
}