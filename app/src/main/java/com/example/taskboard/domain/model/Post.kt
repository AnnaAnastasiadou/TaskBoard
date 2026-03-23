package com.example.taskboard.domain.model

import androidx.room.PrimaryKey

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val tags: List<String>,
    val updatedAt: String?
)