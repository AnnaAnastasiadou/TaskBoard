package com.example.taskboard.data.dto

import androidx.room.PrimaryKey

data class TodoDto (
    val id: Int,
    val userId: Int,
    val todo: String,
    val completed: Boolean,
)