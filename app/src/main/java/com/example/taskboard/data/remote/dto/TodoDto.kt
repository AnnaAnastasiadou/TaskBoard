package com.example.taskboard.data.remote.dto

data class TodoDto (
    val id: Int,
    val userId: Int,
    val todo: String,
    val completed: Boolean,
)