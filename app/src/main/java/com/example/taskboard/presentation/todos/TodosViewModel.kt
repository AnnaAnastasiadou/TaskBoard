package com.example.taskboard.presentation.todos

import androidx.lifecycle.viewModelScope
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.model.Todo
import com.example.taskboard.domain.repository.TodosRepository
import com.example.taskboard.presentation.common.NetworkMonitor
import com.example.taskboard.presentation.common.pagination.BasePaginationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodosViewModel @Inject constructor(
    private val todosRepository: TodosRepository,
    networkMonitor: NetworkMonitor
) : BasePaginationViewModel<Todo>(networkMonitor) {
    private val _errorChannel = Channel<String>()
    val errorEvent = _errorChannel.receiveAsFlow()
    override val dataFlow = todosRepository.getAllTodos().map { entities ->
        entities?.map { it.toDomain() }
            ?: emptyList()
    }

    override fun loadNextBatch() {
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
            when(val result = todosRepository.toggleStatus(id)) {
                is NetworkResult.Success -> {}
                is NetworkResult.Error -> _errorChannel.send(result.message)
                is NetworkResult.NetworkError -> _errorChannel.send(result.message)
            }

        }
    }

}