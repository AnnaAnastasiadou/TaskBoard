package com.example.taskboard.domain.model


data class Todo(
    val id: Int,
    val isLocal: Boolean = false,
    val userId: Int,
    val title: String,
    val completed: Boolean,
    val updatedAt: Long? = null
)
