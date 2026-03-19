package com.example.taskboard.data.remote.api

import com.example.taskboard.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserDto>
}