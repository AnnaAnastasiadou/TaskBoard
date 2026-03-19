package com.example.taskboard.di

import com.example.taskboard.core.TokenProvider
import com.example.taskboard.core.TokenProviderImpl
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
    abstract fun bindTokenProvider(
        tokenProviderImpl: TokenProviderImpl
    ): TokenProvider
}