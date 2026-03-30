package com.example.taskboard.data.remote.api

import com.example.taskboard.data.remote.dto.PostDto
import com.example.taskboard.data.remote.response.PostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    @GET("posts")
    suspend fun getPosts(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): Response<PostResponse>

    @POST("posts/add")
    suspend fun addPost(@Body post: PostDto) : Response<PostDto>

    @PATCH("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body updates: @JvmSuppressWildcards Map<String, Any>) : Response<PostDto>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<PostDto>
}