package com.example.taskboard.domain.mapper

import com.example.taskboard.data.local.entity.TodoEntity
import com.example.taskboard.data.remote.dto.TodoDto
import com.example.taskboard.domain.model.Todo

fun TodoEntity.toDomain(): Todo {
    return Todo(
        id = id,
        isLocal = isLocal,
        userId = userId,
        title = todo,
        completed = completed,
        updatedAt = updatedAt
    )
}

fun Todo.toDto(): TodoDto {
    return TodoDto(
        id = id,
        userId = userId,
        todo = title,
        completed = completed
    )
}