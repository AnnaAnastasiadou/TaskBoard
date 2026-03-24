package com.example.taskboard.domain.mapper

import com.example.taskboard.data.local.entity.PostEntity
import com.example.taskboard.domain.model.Post

fun PostEntity.toDomain(): Post {
    return Post(
        id = id,
        userId = userId,
        title = title,
        body = body,
        tags = tags,
        updatedAt = updatedAt,
        likes = likes,
        dislikes = dislikes
    )
}