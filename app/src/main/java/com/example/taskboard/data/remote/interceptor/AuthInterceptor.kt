package com.example.taskboard.data.remote.interceptor

import com.example.taskboard.data.local.preferences.SharedPreferencesDatasource
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sharedPreferencesDatasource: SharedPreferencesDatasource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferencesDatasource.getAccessToken()

        val request = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder().header("Authorization", "Bearer $token").build()
        }
        return chain.proceed(request)
    }
}