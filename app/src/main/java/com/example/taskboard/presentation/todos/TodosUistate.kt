package com.example.taskboard.presentation.todos

import com.example.taskboard.domain.model.Post

data class TodosUiState(
    val isLoading: Boolean = false,
    val data: List<Post>? = null,
    val error: String? = null,
    val networkError: String? = null
)
