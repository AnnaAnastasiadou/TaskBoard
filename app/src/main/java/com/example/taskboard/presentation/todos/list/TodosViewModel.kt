package com.example.taskboard.presentation.todos.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.model.Todo
import com.example.taskboard.domain.repository.TodosRepository
import com.example.taskboard.presentation.common.NetworkMonitor
import com.example.taskboard.presentation.common.pagination.BasePaginationViewModel
import com.example.taskboard.presentation.common.pagination.BaseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
class TodosViewModel @Inject constructor(
    private val todosRepository: TodosRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    private val _errorChannel = Channel<String>()
    val errorEvent = _errorChannel.receiveAsFlow()

    val localDataFlow = todosRepository.observeLocalTodos()
        .map { entities ->
            entities.map { it.toDomain() }
        }
    val remoteDataFlow = todosRepository.observeRemoteTodos()
        .map { entities ->
            entities.map { it.toDomain() }
        }

    protected var currentSkip = 0
    protected var pageSize = 30
    protected var isFetching = false

    private val _uiState = MutableStateFlow(TodosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeLocalData()
        observeRemoteData()
        observeNetwork()
        loadNextBatch()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            localDataFlow.collect { list ->
                _uiState.update { it.copy(localData = list) }
            }
        }
    }

    private fun observeRemoteData() {
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


    fun loadNextBatch() {
        if (isFetching) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            isFetching = true

            when (val result = todosRepository.refreshAllTodos(30, currentSkip)) {
                is NetworkResult.Success -> {
                    currentSkip += pageSize
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            networkError = null
                        )
                    }
                }

                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.message,
                        networkError = null
                    )
                }

                is NetworkResult.NetworkError -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        networkError = result.message
                    )
                }
            }
            isFetching = false

        }
    }

    fun toggleTodoStatus(id: Int) {
        viewModelScope.launch {
            when (val result = todosRepository.toggleStatus(id)) {
                is NetworkResult.Success -> {}
                is NetworkResult.Error -> _errorChannel.send(result.message)
                is NetworkResult.NetworkError -> _errorChannel.send(result.message)
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