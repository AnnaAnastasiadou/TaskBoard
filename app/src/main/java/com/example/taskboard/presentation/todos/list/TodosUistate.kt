package com.example.taskboard.presentation.todos.list

import com.example.taskboard.domain.model.Todo

data class TodosUiState(
    val isLoading: Boolean = false,
    val localData: List<Todo>? = null,
    val remoteData: List<Todo>? = null,
    val error: String? = null,
    val networkError: String? = null
)