package com.example.taskboard.data.remote.response

import com.example.taskboard.data.remote.dto.TodoDto


data class TodoResponse(
    val todos: List<TodoDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
