package com.example.taskboard.data.remote.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)