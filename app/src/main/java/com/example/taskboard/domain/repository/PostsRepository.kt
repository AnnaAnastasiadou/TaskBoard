package com.example.taskboard.domain.repository

import com.example.taskboard.data.local.entity.PostEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.dto.PostDto
import com.example.taskboard.data.remote.response.PostResponse
import com.example.taskboard.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    fun getAllPosts(): Flow<List<PostEntity>?>
    suspend fun refreshPosts(limit: Int, skip: Int): NetworkResult<PostResponse>
    suspend fun getPostById(id: Int): PostEntity?
    suspend fun updatePost(id: Int, body: Map<String, Any>): NetworkResult<PostDto>
    suspend fun deletePost(postId: Int) : NetworkResult<PostDto>
    suspend fun addPost(post: PostDto) : NetworkResult<PostDto>
}