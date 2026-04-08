package com.example.taskboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val isLocal: Boolean = false,
    val userId: Int,
    val todo: String,
    val completed: Boolean,
    val updatedAt: Long?
)