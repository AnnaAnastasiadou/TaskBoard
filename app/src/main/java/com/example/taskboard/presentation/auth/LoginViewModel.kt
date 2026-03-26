package com.example.taskboard.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    fun logIn(username: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isLoggedIn = false) }
            when (val result = authRepository.logIn(username, password)) {
                is NetworkResult.Success -> _uiState.update { it.copy(isLoading = false, error = null, isLoggedIn = true) }
                is NetworkResult.Error -> _uiState.update { it.copy(isLoading = false, error = result.message, isLoggedIn = false) }
                is NetworkResult.NetworkError -> _uiState.update { it.copy(isLoading = false, error = null, isLoggedIn = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}