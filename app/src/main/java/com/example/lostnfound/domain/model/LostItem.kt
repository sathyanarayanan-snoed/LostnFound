package com.example.lostnfound.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LostItem(
    val id: String = "",
    val reporterId: String = "",
    val ownerName: String = "",
    val ownerContact: String = "",
    val description: String = "",
    val category: String = ItemCategory.OTHER.name,
    val imageUrl: String = "",
    val proofOfOwnership: String = "",
    val proofImageUrl: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val lastSeenLocation: String = "",
    val lostDate: Long = System.currentTimeMillis(),
    val reportedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L,
    val claimed: Boolean = false,
    val status: String = "active",
    val flagged: Boolean = false
)
