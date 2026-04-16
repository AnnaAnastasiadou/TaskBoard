package com.example.taskboard.data

import com.example.taskboard.data.local.entity.PostEntity
import com.example.taskboard.data.remote.dto.PostDto
import com.example.taskboard.data.remote.dto.ReactionsDto
import com.example.taskboard.data.remote.response.PostResponse
import com.example.taskboard.domain.model.Post

val dummyPostResponse = PostResponse(
    posts = listOf(
        PostDto(
            id = 1,
            userId = 101,
            title = "Mastering Kotlin Coroutines",
            body = "Coroutines simplify asynchronous programming by providing a way to write non-blocking code that looks like synchronous code.",
            tags = listOf("kotlin", "android", "concurrency"),
            reactions = ReactionsDto(likes = 150, dislikes = 2)
        ), PostDto(
            id = 2,
            userId = 102,
            title = "Jetpack Compose Best Practices",
            body = "Compose is a modern toolkit for building native UI. Remember to keep your composables stateless by using state hoisting.",
            tags = listOf("android", "ui", "jetpack"),
            reactions = ReactionsDto(likes = 85, dislikes = 0)
        ), PostDto(
            id = 3,
            userId = 103,
            title = "Dependency Injection with Hilt",
            body = "Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.",
            tags = listOf("android", "dagger", "hilt"),
            reactions = ReactionsDto(likes = 42, dislikes = 1)
        )
    ), total = 50, skip = 0, limit = 30
)

val dummyPostsEntity = listOf(
    PostEntity(
        id = 1,
        userId = 101,
        title = "Mastering Kotlin Coroutines",
        body = "Coroutines simplify asynchronous programming by providing a way to write non-blocking code that looks like synchronous code.",
        tags = listOf("kotlin", "android", "concurrency"),
        isLocal = false,
        updatedAt = null,
        likes = 150,
        dislikes = 2
    ),

    PostEntity(
        id = 2,
        userId = 101,
        title = "Jetpack Compose Best Practices",
        body = "Compose is a modern toolkit for building native UI. Remember to keep your composables stateless by using state hoisting.",
        tags = listOf("android", "ui", "jetpack"),
        isLocal = false,
        updatedAt = null,
        likes = 85,
        dislikes = 0
    ), PostEntity(
        id = 3,
        userId = 103,
        title = "Dependency Injection with Hilt",
        body = "Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.",
        tags = listOf("android", "dagger", "hilt"),
        isLocal = false,
        updatedAt = null,
        likes = 42,
        dislikes = 1
    )

)

val dummyPostList = listOf(
    Post(
        id = 1,
        userId = 101,
        title = "Mastering Kotlin Coroutines",
        body = "Coroutines simplify asynchronous programming by providing a way to write non-blocking code that looks like synchronous code.",
        tags = listOf("kotlin", "android", "concurrency"),
        isLocal = false,
        updatedAt = null,
        likes = 150,
        dislikes = 2
    ), Post(
        id = 2,
        userId = 101,
        title = "Jetpack Compose Best Practices",
        body = "Compose is a modern toolkit for building native UI. Remember to keep your composables stateless by using state hoisting.",
        tags = listOf("android", "ui", "jetpack"),
        isLocal = false,
        updatedAt = null,
        likes = 85,
        dislikes = 0
    ), Post(
        id = 3,
        userId = 103,
        title = "Dependency Injection with Hilt",
        body = "Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.",
        tags = listOf("android", "dagger", "hilt"),
        isLocal = false,
        updatedAt = null,
        likes = 42,
        dislikes = 1
    )
)