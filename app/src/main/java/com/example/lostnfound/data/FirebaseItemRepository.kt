package com.example.lostnfound.data

import com.example.lostnfound.data.firebase.FirestoreService
import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters
import com.example.lostnfound.domain.model.User
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseItemRepository(
    private val firestoreService: FirestoreService
) : ItemRepository {

    override suspend fun addFoundItem(item: FoundItem): Result<String> {
        if (!firestoreService.canUserPost(item.reporterId)) {
            return Result.failure(Exception("Post limit reached. Try again later."))
        }
        val result = firestoreService.addFoundItem(item)
        if (result.isSuccess) {
            checkForMatchesAndNotify(item)
        }
        return result
    }

    override suspend fun addLostItem(item: LostItem): Result<String> {
        if (!firestoreService.canUserPost(item.reporterId)) {
            return Result.failure(Exception("Post limit reached. Try again later."))
        }
        return firestoreService.addLostItem(item)
    }

    private suspend fun checkForMatchesAndNotify(foundItem: FoundItem) {
        val filters = SearchFilters(
            query = foundItem.description.take(10),
            category = com.example.lostnfound.domain.model.ItemCategory.valueOf(foundItem.category)
        )
        val matches = firestoreService.searchLostItems(filters).getOrNull()
        if (!matches.isNullOrEmpty()) {
            FirebaseMessaging.getInstance().subscribeToTopic("matches_${foundItem.category}")
        }
    }

    override suspend fun getActiveFoundItems(lastTimestamp: Long?): Result<List<FoundItem>> {
        return firestoreService.getActiveFoundItems(lastTimestamp)
    }

    override suspend fun getActiveLostItems(lastTimestamp: Long?): Result<List<LostItem>> {
        return firestoreService.getActiveLostItems(lastTimestamp)
    }

    override suspend fun searchFoundItems(filters: SearchFilters): Result<List<FoundItem>> {
        return firestoreService.searchFoundItems(filters)
    }

    override suspend fun searchLostItems(filters: SearchFilters): Result<List<LostItem>> {
        return firestoreService.searchLostItems(filters)
    }

    override suspend fun markAsClaimed(itemId: String, isLostItem: Boolean): Result<Unit> {
        return firestoreService.markAsClaimed(itemId, isLostItem)
    }

    override suspend fun getFoundItemById(itemId: String): Result<FoundItem> {
        return firestoreService.getFoundItemById(itemId)
    }

    override suspend fun getLostItemById(itemId: String): Result<LostItem> {
        return firestoreService.getLostItemById(itemId)
    }

    override suspend fun getUserFoundItems(userId: String): Result<List<FoundItem>> {
        return firestoreService.getUserFoundItems(userId)
    }

    override suspend fun getUserLostItems(userId: String): Result<List<LostItem>> {
        return firestoreService.getUserLostItems(userId)
    }

    override suspend fun searchUsers(query: String): Result<List<User>> {
        return Result.success(emptyList())
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return Result.success(emptyList())
    }

    override suspend fun updateUserRole(uid: String, role: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun deleteItemAsAdmin(itemId: String, isLostItem: Boolean): Result<Unit> {
        return Result.success(Unit)
    }
}
