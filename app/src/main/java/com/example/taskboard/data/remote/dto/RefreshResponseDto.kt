package com.example.taskboard.data.remote.dto

data class RefreshResponseDto(
    val accessToken: String,
    val refreshToken: String
)