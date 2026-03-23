package com.example.taskboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity (
    @PrimaryKey val id: Int,
    val userId: Int,
    val todo: String,
    val completed: Boolean,
    val updatedAt: Long?
)