package com.example.taskboard.domain.mapper

import com.example.taskboard.data.local.entity.UserEntity
import com.example.taskboard.domain.model.User

fun UserEntity.toDomain() : User {
    return User(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        image = image
    )
}