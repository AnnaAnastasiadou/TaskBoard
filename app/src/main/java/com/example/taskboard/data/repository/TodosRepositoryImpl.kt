package com.example.taskboard.data.repository

import com.example.taskboard.data.local.dao.TodoDao
import com.example.taskboard.data.local.entity.TodoEntity
import com.example.taskboard.data.mapper.toDto
import com.example.taskboard.data.mapper.toEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.api.TodoApi
import com.example.taskboard.data.remote.dto.TodoDto
import com.example.taskboard.data.remote.response.TodoResponse
import com.example.taskboard.domain.repository.TodosRepository
import com.example.taskboard.presentation.common.getCurrentDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodosRepositoryImpl @Inject constructor(
    private val todoApi: TodoApi,
    private val todoDao: TodoDao
) : TodosRepository {
    override fun getAllTodos(): Flow<List<TodoEntity>?> = todoDao.getTodos()

    override suspend fun refreshAllTodos(limit: Int, skip: Int): NetworkResult<TodoResponse> {
        val result = safeCall { todoApi.getTodos(limit, skip) }
        if (result is NetworkResult.Success) {
            todoDao.insertTodos(result.data.todos.map{ it.toEntity()})
        }
        return result
    }

    override suspend fun getTodoById(id: Int): TodoEntity? = todoDao.getTodoById(id)

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

    override suspend fun deleteTodo(todoId: Int): NetworkResult<TodoDto> {
        val response = safeCall { todoApi.deleteTodo(todoId) }
        if (response is NetworkResult.Success) {
            todoDao.deleteTodo(todoId)
        }
        return response
    }

    override suspend fun addTodo(todo: TodoDto): NetworkResult<TodoDto> {
        val response = safeCall { todoApi.addTodo(todo) }
        if (response is NetworkResult.Success) {
            todoDao.addTodo(todo.toEntity())
        }
        return response
    }

    override suspend fun toggleStatus(id: Int): NetworkResult<TodoDto> {
        val todo =
            todoDao.getTodoById(id) ?: return NetworkResult.Error("Couldn't find todo with id: $id")
        val newStatus = !todo.completed
        val response = safeCall { todoApi.updateTodo(id, mapOf("completed" to newStatus)) }
        if (response is NetworkResult.Success) {
            val updatedTodo = todo.copy(completed = newStatus)
            todoDao.updateTodo(updatedTodo)
            return NetworkResult.Success(data = updatedTodo.toDto())
        }
        return response
    }
}