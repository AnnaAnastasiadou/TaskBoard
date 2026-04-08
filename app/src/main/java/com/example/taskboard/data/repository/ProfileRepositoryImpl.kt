package com.example.taskboard.data.repository

import com.example.taskboard.data.local.dao.ProfileDao
import com.example.taskboard.data.local.entity.UserEntity
import com.example.taskboard.data.mapper.toEntity
import com.example.taskboard.data.remote.NetworkResult
import com.example.taskboard.data.remote.api.ProfileApi
import com.example.taskboard.data.remote.dto.UserDto
import com.example.taskboard.domain.model.User
import com.example.taskboard.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApi: ProfileApi,
    private val profileDao: ProfileDao
): ProfileRepository {
    override suspend fun refreshUserProfile(): NetworkResult<UserDto> {
        val response = safeCall{profileApi.getCurrentUser()}
        if (response is NetworkResult.Success) {
            withContext(Dispatchers.IO) {
                profileDao.insertUser(response.data.toEntity())
            }
        }
        return response
    }

    override fun getUserProfile(): Flow<UserEntity?> = profileDao.getCurrentUser()

    override suspend fun getUserId(): Int = withContext(Dispatchers.IO) { profileDao.getUserId() }

    override suspend fun isProfileDbEmpty(): Boolean = withContext(Dispatchers.IO) { profileDao.getUserCount() == 0 }
}