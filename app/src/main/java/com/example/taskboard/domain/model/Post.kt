package com.example.taskboard.domain.model

data class Post(
    val id: Int,
    val isLocal: Boolean = false,
    val userId: Int,
    val title: String,
    val body: String,
    val tags: List<String>,
    val updatedAt: Long? = null,
    val likes: Int,
    val dislikes: Int
)