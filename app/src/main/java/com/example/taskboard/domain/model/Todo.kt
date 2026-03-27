package com.example.taskboard.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Todo(
    val id: Int,
    val userId: Int,
    val todo: String,
    val completed: Boolean,
    val updatedAt: Long?
)
