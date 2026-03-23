package com.example.taskboard.di

import com.example.taskboard.data.repository.AuthRepositoryImpl
import com.example.taskboard.data.repository.PostsRepositoryImpl
import com.example.taskboard.data.repository.ProfileRepositoryImpl
import com.example.taskboard.data.repository.TodosRepositoryImpl
import com.example.taskboard.domain.repository.AuthRepository
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.domain.repository.ProfileRepository
import com.example.taskboard.domain.repository.TodosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl) : AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl) : ProfileRepository

    @Binds
    @Singleton
    abstract fun bindPostsRepository(postsRepositoryImpl: PostsRepositoryImpl) : PostsRepository

    @Binds
    @Singleton
    abstract fun bindTodosRepository(todosRepositoryImpl: TodosRepositoryImpl) : TodosRepository
}