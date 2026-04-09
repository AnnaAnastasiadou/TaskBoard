package com.example.taskboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskboard.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPost(post: PostEntity)

    @Query("SELECT * FROM posts")
    fun getPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isLocal = 1 ORDER BY id DESC")
    fun observeLocalPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isLocal = 0 ORDER BY id DESC")
    fun observeRemotePosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts ORDER BY isLocal DESC, id DESC")
    fun getAllPostsSorted(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: Int): PostEntity?

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Int)
}