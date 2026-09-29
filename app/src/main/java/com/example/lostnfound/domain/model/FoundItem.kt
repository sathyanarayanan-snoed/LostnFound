package com.example.lostnfound.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FoundItem(
    val id: String = "",
    val reporterId: String = "",
    val finderName: String = "",
    val finderContact: String = "",
    val placeFound: String = "",
    val description: String = "",
    val category: String = ItemCategory.OTHER.name,
    val imageUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val reportedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L,
    val claimed: Boolean = false,
    val status: String = "active"
)
