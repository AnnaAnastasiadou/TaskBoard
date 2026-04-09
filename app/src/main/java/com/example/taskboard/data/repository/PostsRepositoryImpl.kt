package com.example.taskboard.data.repository

import com.example.taskboard.data.local.dao.PostDao
import com.example.taskboard.data.local.entity.PostEntity
import com.example.taskboard.data.mapper.toDto
import com.example.taskboard.data.mapper.toEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.api.PostApi
import com.example.taskboard.data.remote.dto.PostDto
import com.example.taskboard.data.remote.response.PostResponse
import com.example.taskboard.domain.mapper.toDto
import com.example.taskboard.domain.model.Post
import com.example.taskboard.domain.repository.PostsRepository
import com.example.taskboard.presentation.common.getCurrentDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostsRepositoryImpl @Inject constructor(
    private val postApi: PostApi, private val postDao: PostDao
) : PostsRepository {
    private var totalPostsOnServer: Int? = null
    override fun observeLocalPosts(): Flow<List<PostEntity>> = postDao.observeLocalPosts()
    override fun observeRemotePosts(): Flow<List<PostEntity>> = postDao.observeRemotePosts()
    override suspend fun refreshPosts(limit: Int, skip: Int): NetworkResult<PostResponse> {
        val result = safeCall { postApi.getPosts(limit, skip) }
        if (result is NetworkResult.Success) {
            postDao.insertPosts(result.data.posts.map { it.toEntity() })
        }
        return result
    }

    override suspend fun getPostById(id: Int): PostEntity? = postDao.getPostById(id)

    override suspend fun updatePost(id: Int, body: Map<String, Any>): NetworkResult<PostDto> {
        val existingPost = postDao.getPostById(id) ?: return NetworkResult.Error("Couldn't find post with id $id")
        if (existingPost.isLocal) {
            val updatedLocalEntity = existingPost.copy(
                title = body["title"] as? String ?: existingPost.title,
                body = body["body"] as? String ?: existingPost.body,
                tags = (body["tags"] as? List<*>)?.filterIsInstance<String>() ?: existingPost.tags,
                updatedAt = getCurrentDate()
            )

            postDao.updatePost(updatedLocalEntity)
            return NetworkResult.Success(updatedLocalEntity.toDto())
        }
        val response = safeCall { postApi.updatePost(id, body) }
        if (response is NetworkResult.Success) {
            val updatedEntity = response.data.toEntity().copy(
                updatedAt = getCurrentDate()
            )
            postDao.updatePost(updatedEntity)
        }
        return response
    }

    override suspend fun deletePost(post: Post): NetworkResult<PostDto> {
        if (post.isLocal) {
            postDao.deletePost(post.id)
            return NetworkResult.Success(post.toDto())
        }
        val response = safeCall { postApi.deletePost(post.id) }
        if (response is NetworkResult.Success) {
            postDao.deletePost(post.id)
        }
        return response
    }

    override suspend fun addPost(post: PostDto): NetworkResult<PostDto> {
        val response = safeCall { postApi.addPost(post) }
        if (response is NetworkResult.Success) {
            postDao.addPost(post.toEntity(isLocal = true))
        }
        return response
    }
}