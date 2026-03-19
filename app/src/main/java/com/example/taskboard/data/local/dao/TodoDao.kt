package com.example.taskboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskboard.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertTodos(todos: List<TodoEntity>)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertTodo(todo: TodoEntity)

    // READ
    @Query("SELECT * FROM todos")
    fun getTodos(): Flow<List<TodoEntity>?>

    @Query("SELECT * FROM todos WHERE id = :todoId")
    fun getTodoById(todoId: Int): Flow<TodoEntity?>

    // UPDATE
    @Update
    suspend fun updateTodo(todo: TodoEntity)

    // DELETE
    @Query("DELETE FROM todos WHERE id = :todoId")
    suspend fun deleteTodo(todoId: Int)
}