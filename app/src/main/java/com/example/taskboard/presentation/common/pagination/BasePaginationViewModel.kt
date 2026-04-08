package com.example.taskboard.presentation.common.pagination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.presentation.common.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BasePaginationViewModel<T> (
    protected val networkMonitor: NetworkMonitor
) : ViewModel() {
    abstract val localDataFlow: Flow<List<T>>
    abstract val remoteDataFlow: Flow<List<T>>
    protected val _uiState = MutableStateFlow(BaseUiState<T>())
    val uiState = _uiState.asStateFlow()

    protected var currentSkip = 0
    protected var pageSize = 30
    protected var isFetching = false

    init {
        observeLocalData()
        observeRemoteData()
        observeNetwork()
    }
    abstract fun loadNextBatch()
    protected fun observeLocalData() {
        viewModelScope.launch {
            localDataFlow.collect { list ->
                _uiState.update { it.copy(localData = list) }
            }
        }
    }

    protected fun observeRemoteData() {
        viewModelScope.launch {
            remoteDataFlow.collect { list ->
                _uiState.update { it.copy(remoteData = list) }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                val hasNetworkError = _uiState.value.networkError != null
                if (online && hasNetworkError) {
                    onRetry()
                }
            }
        }
    }

    fun onScrollReachedIndex(index: Int) {
        val hasError = uiState.value.error != null || uiState.value.networkError != null

        val localList = uiState.value.localData ?: emptyList()
        val remoteList = uiState.value.remoteData ?: emptyList()
        val localHeader = if (localList.isNotEmpty()) 1 else 0
        val remoteHeader = if (remoteList.isNotEmpty()) 1 else 0
        val totalItems = localList.size + remoteList.size + localHeader + remoteHeader

        if (index >= totalItems - 5 && !isFetching && !hasError) {
            loadNextBatch()
        }
    }

    fun onRetry() {
        loadNextBatch()
    }
}