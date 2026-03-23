package com.example.taskboard.data.mapper

import com.example.taskboard.data.remote.dto.PostDto
import com.example.taskboard.data.local.entity.PostEntity

fun PostDto.toEntity(lastUpdatedAt: Long? = null): PostEntity {
    return PostEntity(
        id = id,
        userId = userId,
        title = title,
        body = body,
        tags = tags,
        updatedAt = lastUpdatedAt,
        likes = reactions.likes,
        dislikes = reactions.dislikes
    )
}