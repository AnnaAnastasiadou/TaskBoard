package com.example.taskboard.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.presentation.main.RootUiState
import com.example.taskboard.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    val uiState: StateFlow<RootUiState> = authRepository.isLoggedIn.map { loggedIn ->
        RootUiState(loggedIn)
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = RootUiState(authRepository.isLoggedIn.value)
    )
}