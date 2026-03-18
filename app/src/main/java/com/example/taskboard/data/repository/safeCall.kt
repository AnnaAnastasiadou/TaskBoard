package com.example.taskboard.data.repository

import com.example.taskboard.data.remote.NetworkResult
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

suspend fun <T>safeCall(call: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = call()
        val body = response.body()

        if(response.isSuccessful && body != null) {
            NetworkResult.Success(body)
        } else {
            val errorBodyString = response.errorBody()?.string()
            val message = try {
                if(!errorBodyString.isNullOrBlank()) {
                    val trimmed = errorBodyString.trim()
                    if(trimmed.startsWith("{")) {
                        JSONObject(trimmed).optString("message", trimmed)
                    } else {
                        trimmed
                    }
                } else {
                    "An Unexpected Error Occurred"
                }
            } catch (e: Exception) {
                errorBodyString ?: "An Unexpected Error Occurred"
            }
            NetworkResult.Error(message)
        }
    } catch (e: ConnectException) {
        NetworkResult.Error(message = "Unable to reach server")
    } catch (e: UnknownHostException) {
        NetworkResult.Error(message = "No internet connection")
    } catch (e: HttpException) {
        NetworkResult.Error(message = "HTTP ${e.code()}: ${e.message}")
    }
}