package com.example.taskboard.data.remote.api

import com.example.taskboard.data.remote.dto.TodoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): Response<List<TodoDto>>

    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: Int) : Response<List<TodoDto>>

    @POST("todos/add")
    suspend fun addTodo(@Body todo: TodoDto) : Response<TodoDto>

    @PATCH("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Int, @Body updates: Map<String, Any>) : Response<TodoDto>

    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Int): Response<TodoDto>
}