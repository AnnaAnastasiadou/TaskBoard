package com.example.taskboard.di

import com.example.taskboard.core.SessionProvider
import com.example.taskboard.core.SessionProviderImpl
import com.example.taskboard.data.local.preferences.SharedPreferencesDatasource
import com.example.taskboard.data.local.preferences.SharedPreferencesDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    @Singleton
    abstract fun bindSessionManager(sharedPreferencesDatasourceImpl: SharedPreferencesDatasourceImpl): SharedPreferencesDatasource

    @Binds
    @Singleton
    abstract fun bindSessionProvider(
        sessionProviderImpl: SessionProviderImpl
    ): SessionProvider
}