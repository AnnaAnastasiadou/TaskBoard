package com.example.taskboard.di

import android.content.Context
import androidx.room.Room
import com.example.taskboard.data.local.dao.PostDao
import com.example.taskboard.data.local.dao.TodoDao
import com.example.taskboard.data.local.dao.UserDao
import com.example.taskboard.data.local.database.TaskBoardDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTaskBoardDatabase(@ApplicationContext context: Context) : TaskBoardDatabase {
        return Room.databaseBuilder(
            context,
            TaskBoardDatabase::class.java,
            "task_board_db"
        ).build()
    }

    @Provides
    fun providePostDao(database: TaskBoardDatabase): PostDao = database.postDao()

    @Provides
    fun provideTodoDao(database: TaskBoardDatabase): TodoDao = database.todoDao()

    @Provides
    fun provideUserDao(database: TaskBoardDatabase): UserDao = database.userDao()
}