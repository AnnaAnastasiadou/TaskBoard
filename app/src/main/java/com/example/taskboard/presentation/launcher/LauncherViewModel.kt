package com.example.taskboard.presentation.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    val uiState: StateFlow<LauncherUiState> = authRepository.isLoggedIn.map { loggedIn ->
        LauncherUiState(loggedIn, false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = LauncherUiState(authRepository.isLoggedIn.value, true)
    )
}