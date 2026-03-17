package com.example.taskboard.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskboard.data.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPost(post: PostEntity)

    // READ
    @Query("SELECT * FROM posts")
    fun getPosts(): Flow<List<PostEntity>?>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Int): Flow<PostEntity?>

    // UPDATE
    @Update
    suspend fun updatePost(post: PostEntity)

    // DELETE
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Int)
}