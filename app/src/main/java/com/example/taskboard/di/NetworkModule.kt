package com.example.taskboard.di

import com.example.taskboard.data.remote.api.AuthApi
import com.example.taskboard.data.remote.api.PostApi
import com.example.taskboard.data.remote.api.ProfileApi
import com.example.taskboard.data.remote.api.TodoApi
import com.example.taskboard.data.remote.interceptor.AuthInterceptor
import com.example.taskboard.data.remote.interceptor.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, authInterceptor: AuthInterceptor, tokenAuthenticator: TokenAuthenticator): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient).build()
    }

    @Singleton
    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Singleton
    @Provides
    fun providePostApi(retrofit: Retrofit): PostApi = retrofit.create(PostApi::class.java)

    @Singleton
    @Provides
    fun provideTodoApi(retrofit: Retrofit): TodoApi = retrofit.create(TodoApi::class.java)

    @Singleton
    @Provides
    fun provideProfileApi(retrofit: Retrofit): ProfileApi = retrofit.create(ProfileApi::class.java)
}