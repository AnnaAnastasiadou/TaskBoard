package com.example.taskboard.data.remote.api

import com.example.taskboard.data.remote.dto.PostDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApi {
    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int) : Response<List<PostDto>>

    @POST("posts/add")
    suspend fun addPost(@Body post: PostDto) : Response<PostDto>

    @PATCH("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body updates: Map<String, Any>) : Response<PostDto>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<PostDto>
}