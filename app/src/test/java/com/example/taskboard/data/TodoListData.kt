package com.example.taskboard.data

import com.example.taskboard.data.local.entity.TodoEntity
import com.example.taskboard.data.remote.dto.TodoDto
import com.example.taskboard.data.remote.response.TodoResponse

val dummyTodoListDto = listOf(
    TodoDto(id = 1, todo = "Setup Project", completed = true, userId = 1),
    TodoDto(id = 2, todo = "Install Dependencies", completed = true, userId = 1),
    TodoDto(id = 3, todo = "Configure Linting", completed = false, userId = 2)
)

val dummyTodosResponse = TodoResponse(
    todos = dummyTodoListDto,
    total = 90,
    skip = 0,
    limit = 30
)

val dummyTodoListEntity = listOf(
    TodoEntity(
        id = 1,
        isLocal = true,
        userId = 1,
        todo = "Setup Project",
        completed = true,
        updatedAt = null
    ), TodoEntity(
        id = 2,
        isLocal = false,
        userId = 1,
        todo = "Install Dependencies",
        completed = true,
        updatedAt = null
    ), TodoEntity(
        id = 3,
        isLocal = false,
        userId = 2,
        todo = "Configure Linting",
        completed = false,
        updatedAt = null
    )
)