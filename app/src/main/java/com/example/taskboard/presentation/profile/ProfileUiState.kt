package com.example.taskboard.presentation.profile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val networkError: String? = null
)