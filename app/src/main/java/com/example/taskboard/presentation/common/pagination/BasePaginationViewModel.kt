package com.example.taskboard.presentation.common.pagination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.presentation.common.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BasePaginationViewModel<T> (
    protected val networkMonitor: NetworkMonitor
) : ViewModel() {
    abstract val dataFlow: Flow<List<T>>
    protected val _uiState = MutableStateFlow(BaseUiState<T>())
    val uiState = _uiState.asStateFlow()

    protected var currentSkip = 0
    protected var pageSize = 30
    protected var isFetching = false

    init {
        subscribeToData()
        observeNetwork()
    }

    abstract fun loadNextBatch()

    protected fun subscribeToData() {
        viewModelScope.launch {
            dataFlow.collect { list ->
                _uiState.update { it.copy(data = list) }
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
        val totalItems = uiState.value.data?.size ?: 0
        if (index >= totalItems - 5 && !isFetching && !hasError) {
            loadNextBatch()
        }
    }

    fun onRetry() {
        loadNextBatch()
    }
}