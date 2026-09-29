package com.example.lostnfound.data.firebase

import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()
    private val foundItemsCollection = db.collection("found_items")
    private val lostItemsCollection = db.collection("lost_items")
    private val usersCollection = db.collection("users")

    companion object {
        const val PAGE_SIZE = 20
        const val MAX_POSTS_PER_HOUR = 5
        const val ONE_HOUR_MS = 3600000L
        const val SEVEN_DAYS_MS = 7 * 24 * 60 * 60 * 1000L
        const val MIN_PROOF_LENGTH = 20
    }

    suspend fun addFoundItem(item: FoundItem): Result<String> {
        return try {
            val docRef = foundItemsCollection.document()
            val itemWithId = item.copy(
                id = docRef.id,
                expiresAt = System.currentTimeMillis() + SEVEN_DAYS_MS
            )
            docRef.set(itemWithId).await()
            incrementPostCount(item.reporterId)
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addLostItem(item: LostItem): Result<String> {
        return try {
            val docRef = lostItemsCollection.document()
            val shouldFlag = item.proofOfOwnership.length < MIN_PROOF_LENGTH
            val itemWithId = item.copy(
                id = docRef.id,
                expiresAt = System.currentTimeMillis() + SEVEN_DAYS_MS,
                flagged = shouldFlag
            )
            docRef.set(itemWithId).await()
            incrementPostCount(item.reporterId)
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveFoundItems(lastTimestamp: Long? = null): Result<List<FoundItem>> {
        return try {
            var query = foundItemsCollection
                .whereEqualTo("status", "active")
                .orderBy("reportedAt", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE.toLong())
            if (lastTimestamp != null) {
                query = query.startAfter(lastTimestamp)
            }
            val snapshot = query.get().await()
            val now = System.currentTimeMillis()
            val items = snapshot.toObjects(FoundItem::class.java)
                .filter { it.expiresAt > now }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getActiveLostItems(lastTimestamp: Long? = null): Result<List<LostItem>> {
        return try {
            var query = lostItemsCollection
                .whereEqualTo("status", "active")
                .orderBy("reportedAt", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE.toLong())
            if (lastTimestamp != null) {
                query = query.startAfter(lastTimestamp)
            }
            val snapshot = query.get().await()
            val now = System.currentTimeMillis()
            val items = snapshot.toObjects(LostItem::class.java)
                .filter { it.expiresAt > now }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchFoundItems(filters: SearchFilters): Result<List<FoundItem>> {
        return try {
            var query: Query = foundItemsCollection
                .whereEqualTo("status", "active")
            if (filters.category != null) {
                query = query.whereEqualTo("category", filters.category.name)
            }
            val snapshot = query.get().await()
            val now = System.currentTimeMillis()
            var items = snapshot.toObjects(FoundItem::class.java)
                .filter { it.expiresAt > now }
            if (filters.query.isNotBlank()) {
                val searchLower = filters.query.lowercase()
                items = items.filter {
                    it.description.lowercase().contains(searchLower) ||
                    it.placeFound.lowercase().contains(searchLower) ||
                    it.finderName.lowercase().contains(searchLower)
                }
            }
            if (filters.location.isNotBlank()) {
                items = items.filter {
                    it.placeFound.lowercase().contains(filters.location.lowercase())
                }
            }
            if (filters.dateFrom != null) {
                items = items.filter { it.reportedAt >= filters.dateFrom }
            }
            if (filters.dateTo != null) {
                items = items.filter { it.reportedAt <= filters.dateTo }
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchLostItems(filters: SearchFilters): Result<List<LostItem>> {
        return try {
            var query: Query = lostItemsCollection
                .whereEqualTo("status", "active")
            if (filters.category != null) {
                query = query.whereEqualTo("category", filters.category.name)
            }
            val snapshot = query.get().await()
            val now = System.currentTimeMillis()
            var items = snapshot.toObjects(LostItem::class.java)
                .filter { it.expiresAt > now }
            if (filters.query.isNotBlank()) {
                val searchLower = filters.query.lowercase()
                items = items.filter {
                    it.description.lowercase().contains(searchLower) ||
                    it.ownerName.lowercase().contains(searchLower)
                }
            }
            if (filters.location.isNotBlank()) {
                items = items.filter {
                    it.description.lowercase().contains(filters.location.lowercase())
                }
            }
            if (filters.dateFrom != null) {
                items = items.filter { it.reportedAt >= filters.dateFrom }
            }
            if (filters.dateTo != null) {
                items = items.filter { it.reportedAt <= filters.dateTo }
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsClaimed(itemId: String, isLostItem: Boolean): Result<Unit> {
        return try {
            val collection = if (isLostItem) lostItemsCollection else foundItemsCollection
            collection.document(itemId).update(
                mapOf("claimed" to true, "status" to "claimed")
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFoundItemById(itemId: String): Result<FoundItem> {
        return try {
            val doc = foundItemsCollection.document(itemId).get().await()
            val item = doc.toObject(FoundItem::class.java)
            if (item != null) Result.success(item) else Result.failure(Exception("Item not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLostItemById(itemId: String): Result<LostItem> {
        return try {
            val doc = lostItemsCollection.document(itemId).get().await()
            val item = doc.toObject(LostItem::class.java)
            if (item != null) Result.success(item) else Result.failure(Exception("Item not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFoundItems(userId: String): Result<List<FoundItem>> {
        return try {
            val snapshot = foundItemsCollection
                .whereEqualTo("reporterId", userId)
                .orderBy("reportedAt", Query.Direction.DESCENDING)
                .get().await()
            Result.success(snapshot.toObjects(FoundItem::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserLostItems(userId: String): Result<List<LostItem>> {
        return try {
            val snapshot = lostItemsCollection
                .whereEqualTo("reporterId", userId)
                .orderBy("reportedAt", Query.Direction.DESCENDING)
                .get().await()
            Result.success(snapshot.toObjects(LostItem::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun canUserPost(userId: String): Boolean {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val lastPostTime = doc.getLong("lastPostTime") ?: 0L
            val postCount = doc.getLong("postCount")?.toInt() ?: 0
            val timeSinceLastPost = System.currentTimeMillis() - lastPostTime
            if (timeSinceLastPost > ONE_HOUR_MS) {
                usersCollection.document(userId).update(
                    mapOf("postCount" to 0, "lastPostTime" to System.currentTimeMillis())
                ).await()
                true
            } else {
                postCount < MAX_POSTS_PER_HOUR
            }
        } catch (e: Exception) {
            true
        }
    }

    private suspend fun incrementPostCount(userId: String) {
        try {
            usersCollection.document(userId).update(
                mapOf(
                    "postCount" to FieldValue.increment(1),
                    "lastPostTime" to System.currentTimeMillis()
                )
            ).await()
        } catch (_: Exception) { }
    }

    suspend fun cleanupExpiredItems() {
        try {
            val now = System.currentTimeMillis()
            val batch = db.batch()

            val expiredFound = foundItemsCollection
                .whereEqualTo("status", "active")
                .whereLessThan("expiresAt", now)
                .get().await()
            for (doc in expiredFound.documents) {
                batch.update(doc.reference, "status", "expired")
            }

            val expiredLost = lostItemsCollection
                .whereEqualTo("status", "active")
                .whereLessThan("expiresAt", now)
                .get().await()
            for (doc in expiredLost.documents) {
                batch.update(doc.reference, "status", "expired")
            }

            batch.commit().await()
        } catch (_: Exception) { }
    }

    suspend fun deleteItem(itemId: String, isLostItem: Boolean): Result<Unit> {
        return try {
            val collection = if (isLostItem) lostItemsCollection else foundItemsCollection
            collection.document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
