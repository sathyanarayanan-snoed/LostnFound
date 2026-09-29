package com.example.lostnfound.data

import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters

interface ItemRepository {
    suspend fun addFoundItem(item: FoundItem): Result<String>
    suspend fun addLostItem(item: LostItem): Result<String>
    suspend fun getActiveFoundItems(lastTimestamp: Long? = null): Result<List<FoundItem>>
    suspend fun getActiveLostItems(lastTimestamp: Long? = null): Result<List<LostItem>>
    suspend fun searchFoundItems(filters: SearchFilters): Result<List<FoundItem>>
    suspend fun searchLostItems(filters: SearchFilters): Result<List<LostItem>>
    suspend fun markAsClaimed(itemId: String, isLostItem: Boolean): Result<Unit>
    suspend fun getFoundItemById(itemId: String): Result<FoundItem>
    suspend fun getLostItemById(itemId: String): Result<LostItem>
    suspend fun getUserFoundItems(userId: String): Result<List<FoundItem>>
    suspend fun getUserLostItems(userId: String): Result<List<LostItem>>
}
