package com.example.lostnfound.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val postCount: Int = 0,
    val lastPostTime: Long = 0L
)
