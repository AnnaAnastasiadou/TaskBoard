package com.example.taskboard.presentation.posts

import com.example.taskboard.domain.model.Post

data class PostsUiState (
    val isLoading: Boolean = false,
    val data: List<Post>? = null,
    val error: String? = null,
    val networkError: String? = null
)