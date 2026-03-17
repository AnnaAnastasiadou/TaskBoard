package com.example.taskboard.data.mapper

import com.example.taskboard.data.dto.UserDto
import com.example.taskboard.data.entity.UserEntity

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