package com.example.taskboard.domain.mapper

import com.example.taskboard.data.local.entity.TodoEntity
import com.example.taskboard.domain.model.Todo

fun TodoEntity.toDomain(): Todo {
    return Todo(
        id = id,
        userId = userId,
        todo = todo,
        completed = completed,
        updatedAt = updatedAt
    )
}