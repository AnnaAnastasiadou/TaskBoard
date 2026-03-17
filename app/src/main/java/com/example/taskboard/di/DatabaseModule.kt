package com.example.taskboard.di

import android.content.Context
import androidx.room.Room
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
}