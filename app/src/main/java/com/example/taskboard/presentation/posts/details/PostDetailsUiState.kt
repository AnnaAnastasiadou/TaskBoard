package com.example.taskboard.presentation.posts.details

import com.example.taskboard.domain.model.Post

data class PostDetailsUiState(
    val status: ScreenStatus = ScreenStatus.IDLE,
    val data: Post? = null,
    val validationError: PostValidationError = PostValidationError(),
    val snackBarMessage: String? = null
)

data class PostValidationError(
    val titleError: String? = null,
    val bodyError: String? = null,
    val tagsError: String? = null,
    val errorTagIndices: Set<Int> = emptySet()
)

enum class ScreenStatus { IDLE, ERROR, LOADING_DATA, SAVING, DELETING }