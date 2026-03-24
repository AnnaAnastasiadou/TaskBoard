package com.example.taskboard.data.remote.response

import com.example.taskboard.data.remote.dto.PostDto

data class PostResponse(
    val posts: List<PostDto>,
    val total: Int,
    val skp: Int,
    val limit: Int
)
