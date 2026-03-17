package com.example.taskboard.data.dto

import androidx.room.PrimaryKey

data class PostDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val tags: List<String>,
)
