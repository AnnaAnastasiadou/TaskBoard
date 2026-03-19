package com.example.taskboard.data.mapper

import com.example.taskboard.data.remote.dto.TodoDto
import com.example.taskboard.data.local.entity.TodoEntity

fun TodoDto.toEntity(lastUpdated: String? = null): TodoEntity {
    return TodoEntity(
        id = id,
        userId = userId,
        todo = todo,
        completed = completed,
        updatedAt = lastUpdated
    )
}