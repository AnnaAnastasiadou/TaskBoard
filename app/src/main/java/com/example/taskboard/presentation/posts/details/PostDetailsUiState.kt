package com.example.taskboard.presentation.posts.details

import com.example.taskboard.domain.model.Post

data class PostDetailsUiState(
    val isLoading: Boolean = false,
    val data: Post? = null
)
