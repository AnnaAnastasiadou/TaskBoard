package com.example.taskboard.data.remote

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T): NetworkResult<T>()
    data class Error(val message: String): NetworkResult<Nothing>()
    data class NetworkError(val message: String): NetworkResult<Nothing>()
}