package com.example.taskboard.domain.repository

import com.example.taskboard.data.local.entity.TodoEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.dto.TodoDto
import kotlinx.coroutines.flow.Flow

interface TodosRepository {
    fun getAllTodos(): Flow<List<TodoEntity>?>
    suspend fun refreshAllTodos(limit: Int, skip: Int): NetworkResult<List<TodoDto>>
    suspend fun getPostById(id: Int): NetworkResult<TodoDto>
    suspend fun addTodo(todo: TodoDto): NetworkResult<TodoDto>
    suspend fun updateTodo(id: Int, body: Map<String, Any>): NetworkResult<TodoDto>
    suspend fun deleteTodo(todoId: Int): NetworkResult<TodoDto>
}