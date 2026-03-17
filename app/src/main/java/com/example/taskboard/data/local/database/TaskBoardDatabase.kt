package com.example.taskboard.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskboard.data.dao.PostDao
import com.example.taskboard.data.dao.TodoDao
import com.example.taskboard.data.dao.UserDao
import com.example.taskboard.data.entity.PostEntity
import com.example.taskboard.data.entity.TodoEntity
import com.example.taskboard.data.entity.UserEntity

@Database(
    entities = [PostEntity::class, TodoEntity::class, UserEntity::class],
    version = 1
)
abstract class TaskBoardDatabase: RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun todoDao(): TodoDao
    abstract fun userDao(): UserDao
}