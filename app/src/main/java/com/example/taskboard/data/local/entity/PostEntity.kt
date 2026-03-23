package com.example.taskboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val tags: List<String>,
    val updatedAt: Long?,
    val likes: Int,
    val dislikes: Int
)
