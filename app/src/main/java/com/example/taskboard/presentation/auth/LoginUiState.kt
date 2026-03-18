package com.example.taskboard.presentation.auth

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)