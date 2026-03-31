package com.example.taskboard.presentation.profile

import com.example.taskboard.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val networkError: String? = null
)