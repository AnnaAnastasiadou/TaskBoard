package com.example.taskboard.data.remote.dto

import androidx.room.PrimaryKey

data class UserDto(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val image: String?
)