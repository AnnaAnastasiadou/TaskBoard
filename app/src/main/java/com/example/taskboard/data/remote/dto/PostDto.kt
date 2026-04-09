package com.example.taskboard.data.remote.dto

data class PostDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val tags: List<String>,
    val reactions: ReactionsDto
)
