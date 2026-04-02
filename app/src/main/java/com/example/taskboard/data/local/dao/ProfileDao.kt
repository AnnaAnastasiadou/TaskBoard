package com.example.taskboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskboard.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT id FROM users LIMIT 1")
    suspend fun getUserId(): Int

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}