package com.example.taskboard.data.mapper

import com.example.taskboard.data.remote.dto.UserDto
import com.example.taskboard.data.local.entity.UserEntity

fun UserDto.toEntity() : UserEntity {
    return UserEntity(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        image = image
    )
}