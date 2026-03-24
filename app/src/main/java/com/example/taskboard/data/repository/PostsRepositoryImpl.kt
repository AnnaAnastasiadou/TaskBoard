package com.example.taskboard.data.repository

import com.example.taskboard.data.local.dao.PostDao
import com.example.taskboard.data.local.entity.PostEntity
import com.example.taskboard.data.mapper.toEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.api.PostApi
import com.example.taskboard.data.remote.dto.PostDto
import com.example.taskboard.data.remote.response.PostResponse
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.presentation.getCurrentDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(
    private val postApi: PostApi,
    private val postDao: PostDao
) : PostsRepository {
    override fun getAllPosts(): Flow<List<PostEntity>?> = postDao.getPosts()

    override suspend fun refreshPosts(limit: Int, skip: Int): NetworkResult<PostResponse> {
        val result = safeCall { postApi.getPosts(limit, skip) }
        if (result is NetworkResult.Success) {
            postDao.insertPosts(result.data.posts.map { it.toEntity() })
        }
        return result
    }

    override suspend fun getPostById(id: Int): NetworkResult<PostDto> =
        safeCall { postApi.getPostById(id) }

    override suspend fun updatePost(id: Int, body: Map<String, Any>): NetworkResult<PostDto> {
        val response = safeCall { postApi.updatePost(id, body) }
        if (response is NetworkResult.Success) {
            val updatedEntity = response.data.toEntity().copy(
                updatedAt = getCurrentDate()
            )
            postDao.updatePost(updatedEntity)
        }
        return response
    }

    override suspend fun deletePost(postId: Int) : NetworkResult<PostDto>  {
        val response = safeCall{ postApi.deletePost(postId) }
        if (response is NetworkResult.Success) {
            postDao.deletePost(postId)
        }
        return response
    }

    override suspend fun addPost(post: PostDto) : NetworkResult<PostDto>  {
        val response = safeCall{ postApi.addPost(post) }
        if (response is NetworkResult.Success) {
            postDao.addPost(post.toEntity())
        }
        return response
    }
}