package com.example.taskboard.presentation.todos.details

import com.example.taskboard.domain.model.Todo

data class TodoDetailsUiState(
    val status: ScreenStatus = ScreenStatus.IDLE,
    val data: Todo? = null,
    val validationError: TodoValidationError = TodoValidationError(),
    val snackBarMessage: String? = null
)

data class TodoValidationError(
    val titleError: String? = null
)

enum class ScreenStatus { IDLE, ERROR, LOADING_DATA, SAVING, DELETING }