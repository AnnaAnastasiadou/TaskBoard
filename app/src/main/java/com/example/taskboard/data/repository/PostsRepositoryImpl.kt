package com.example.taskboard.data.repository

import com.example.taskboard.data.local.dao.PostDao
import com.example.taskboard.data.local.entity.PostEntity
import com.example.taskboard.data.mapper.toEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.api.PostApi
import com.example.taskboard.data.remote.dto.PostDto
import com.example.taskboard.data.remote.response.PostResponse
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.presentation.common.getCurrentDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.emptyList

class PostsRepositoryImpl @Inject constructor(
    private val postApi: PostApi, private val postDao: PostDao
) : PostsRepository {
    override fun observeLocalPosts(): Flow<List<PostEntity>> = postDao.observeLocalPosts()
    override fun observeRemotePosts(): Flow<List<PostEntity>> = postDao.observeRemotePosts()
    override suspend fun refreshPosts(limit: Int, skip: Int): NetworkResult<PostResponse> {
        val result = safeCall { postApi.getPosts(limit, skip) }
        if (result is NetworkResult.Success) {
            withContext(Dispatchers.IO) {
                postDao.insertPosts(result.data.posts.map { it.toEntity() })
            }
        }
        return result
    }

    override suspend fun getPostById(id: Int): PostEntity? = withContext(Dispatchers.IO) {postDao.getPostById(id)}

    override suspend fun updatePost(id: Int, body: Map<String, Any>): NetworkResult<PostDto> {
        val response = safeCall { postApi.updatePost(id, body) }
        if (response is NetworkResult.Success) {
            withContext(Dispatchers.IO) {
                val existingPost = postDao.getPostById(id)
                val isLocal = existingPost?.isLocal ?: false
                val updatedEntity = response.data.toEntity().copy(
                    isLocal = isLocal,
                    updatedAt = getCurrentDate()
                )
                postDao.updatePost(updatedEntity)
            }
        }
        return response
    }

    override suspend fun deletePost(postId: Int): NetworkResult<PostDto> {
        val response = safeCall { postApi.deletePost(postId) }
        if (response is NetworkResult.Success) {
            withContext(Dispatchers.IO) {
                postDao.deletePost(postId)
            }
        }
        return response
    }

    override suspend fun addPost(post: PostDto): NetworkResult<PostDto> {
        val response = safeCall { postApi.addPost(post) }
        if (response is NetworkResult.Success) {
            withContext(Dispatchers.IO) {
                postDao.addPost(post.toEntity(isLocal = true))
            }
        }
        return response
    }
}