package com.example.taskboard.domain.repository

import com.example.taskboard.data.local.entity.UserEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun refreshUserProfile(): NetworkResult<UserDto>
    fun getUserProfile(): Flow<UserEntity?>
    suspend fun getUserId(): Int
    suspend fun isProfileDbEmpty(): Boolean
}