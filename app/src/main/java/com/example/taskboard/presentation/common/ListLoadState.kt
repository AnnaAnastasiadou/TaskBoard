package com.example.taskboard.presentation.common

sealed class ListLoadState {
    object Hidden : ListLoadState()
    object Loading : ListLoadState()
    class Error(val message: String) : ListLoadState()
    class NetworkError(val message: String) : ListLoadState()
}
