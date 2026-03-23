package com.example.taskboard.data.repository

import com.example.taskboard.data.local.dao.TodoDao
import com.example.taskboard.data.local.entity.PostEntity
import com.example.taskboard.data.local.entity.TodoEntity
import com.example.taskboard.data.mapper.toEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.api.TodoApi
import com.example.taskboard.data.remote.dto.PostDto
import com.example.taskboard.data.remote.dto.TodoDto
import com.example.taskboard.domain.repository.TodosRepository
import com.example.taskboard.presentation.getCurrentDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodosRepositoryImpl @Inject constructor(
    private val todoApi: TodoApi,
    private val todoDao: TodoDao
) : TodosRepository {
    override fun getAllTodos(): Flow<List<TodoEntity>?> = todoDao.getTodos()

    override suspend fun refreshAllTodos(): NetworkResult<List<TodoDto>> = safeCall { todoApi.getTodos() }

    override suspend fun getPostById(id: Int): NetworkResult<TodoDto> =
        safeCall { todoApi.getTodoById(id) }

    override suspend fun updateTodo(id: Int, body: Map<String, Any>): NetworkResult<TodoDto> {
        val response = safeCall { todoApi.updateTodo(id, body) }
        if (response is NetworkResult.Success) {
            val updatedEntity = response.data.toEntity().copy(
                updatedAt = getCurrentDate()
            )
            todoDao.updateTodo(updatedEntity)
        }
        return response
    }

    override suspend fun deleteTodo(todoId: Int) : NetworkResult<TodoDto>  {
        val response = safeCall{ todoApi.deleteTodo(todoId) }
        if (response is NetworkResult.Success) {
            todoDao.deleteTodo(todoId)
        }
        return response
    }

    override suspend fun addTodo(todo: TodoDto) : NetworkResult<TodoDto>  {
        val response = safeCall{ todoApi.addTodo(todo) }
        if (response is NetworkResult.Success) {
            todoDao.addTodo(todo.toEntity())
        }
        return response
    }
}