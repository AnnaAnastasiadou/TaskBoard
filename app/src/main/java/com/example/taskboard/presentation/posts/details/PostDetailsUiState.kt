package com.example.taskboard.presentation.posts.details

import com.example.taskboard.domain.model.Post

data class PostDetailsUiState(
    val isLoading: Boolean = false,
    val data: Post? = null,
    val validationError: PostValidationError = PostValidationError()
)

data class PostValidationError(
    val titleError: String? = null,
    val bodyError: String? = null,
    val tagsError: String? = null,
    val errorTagIndices: Set<Int> = emptySet()
)