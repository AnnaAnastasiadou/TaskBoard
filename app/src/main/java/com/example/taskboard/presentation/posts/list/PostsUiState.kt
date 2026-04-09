package com.example.taskboard.presentation.posts.list

import com.example.taskboard.domain.model.Post

data class PostsUiState(
    val isLoading: Boolean = false,
    val localData: List<Post>? = null,
    val remoteData: List<Post>? = null,
    val error: String? = null,
    val networkError: String? = null
)