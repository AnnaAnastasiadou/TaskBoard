package com.example.taskboard.presentation.todos.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskboard.core.SessionProvider
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.domain.mapper.toDomain
import com.example.taskboard.domain.mapper.toDto
import com.example.taskboard.domain.model.Todo
import com.example.taskboard.domain.repository.TodosRepository
import com.example.taskboard.presentation.posts.details.PostUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoDetailsViewModel @Inject constructor(
    private val todosRepository: TodosRepository,
    private val sessionProvider: SessionProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(TodoDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<TodoUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val todoId: Int = savedStateHandle.get<Int>("todo_id")!!

    init {
        loadTodoDetails()
    }

    private fun loadTodoDetails() {
        viewModelScope.launch {
            if (todoId != -1) {
                _uiState.update { it.copy(status = ScreenStatus.LOADING_DATA, data = null) }
                val todoEntity = todosRepository.getTodoById(todoId)
                if (todoEntity != null) {
                    _uiState.update {
                        it.copy(
                            status = ScreenStatus.IDLE,
                            data = todoEntity.toDomain()
                        )
                    }
                } else {
                    _uiState.update { it.copy(status = ScreenStatus.ERROR, data = null) }
                }
            } else {
                _uiState.update {
                    it.copy(
                        status = ScreenStatus.IDLE,
                        data = Todo(
                            id = 0,
                            userId = sessionProvider.getUserId() ?: 0,
                            title = "",
                            completed = false,
                        )
                    )
                }
            }
        }
    }

    suspend fun saveTodo() {
        val currentTodo = _uiState.value.data ?: return

        val validationErrors = TodoValidationError(
            titleError = if (currentTodo.title.isBlank()) "Title is required" else null
        )

        _uiState.update { it.copy(validationError = validationErrors) }

        if (validationErrors.titleError != null) {
            return
        }

        val requestBody = mapOf("todo" to currentTodo.title)
        val isEditMode = todoId != -1

        val result = if (isEditMode) todosRepository.updateTodo(
            todoId,
            requestBody
        ) else todosRepository.addTodo(currentTodo.toDto())

        _uiState.update { it.copy(status = ScreenStatus.SAVING, snackbarMessage = "Saving...") }
        when (result) {
            is NetworkResult.Error -> _uiState.update {
                it.copy(
                    status = ScreenStatus.IDLE,
                    snackbarMessage = result.message
                )
            }

            is NetworkResult.NetworkError -> _uiState.update {
                it.copy(
                    status = ScreenStatus.IDLE,
                    snackbarMessage = result.message
                )
            }

            is NetworkResult.Success -> {
                _uiState.update {
                    it.copy(
                        status = ScreenStatus.IDLE,
                        snackbarMessage = null
                    )
                }
                _uiEvent.send(TodoUiEvent.NavigateBack)
            }
        }

    }

    suspend fun deleteTodo() {
        val todo = uiState.value.data ?: return
        _uiState.update { it.copy(status = ScreenStatus.DELETING, snackbarMessage = "Deleting...") }
        when (val result = todosRepository.deleteTodo(todo)) {
            is NetworkResult.Success -> {
                _uiState.update { it.copy(snackbarMessage = null) }
                _uiEvent.send(TodoUiEvent.NavigateBack)
            }

            is NetworkResult.Error -> _uiState.update {
                it.copy(status = ScreenStatus.IDLE, snackbarMessage = result.message)
            }

            is NetworkResult.NetworkError -> _uiState.update {
                it.copy(status = ScreenStatus.IDLE, snackbarMessage = result.message)
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(data = it.data?.copy(title = newTitle)) }
    }

    fun onRetry() {
        loadTodoDetails()
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null)}
    }
}