package com.example.taskboard.presentation.common.pagination

import com.example.taskboard.domain.model.Post

data class BaseUiState<T>(
    val isLoading: Boolean = false,
    val data: List<T>? = null,
    val error: String? = null,
    val networkError: String? = null
)