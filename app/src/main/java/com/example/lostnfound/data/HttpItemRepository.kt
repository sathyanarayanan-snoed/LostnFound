package com.example.lostnfound.data

import com.example.lostnfound.data.network.ApiClient
import com.example.lostnfound.data.network.IdResponse
import com.example.lostnfound.data.network.UserItemsResponse
import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters
import kotlinx.serialization.encodeToString
import java.net.URLEncoder

class HttpItemRepository(
    private val apiClient: ApiClient,
    private val authRepository: HttpAuthRepository
) : ItemRepository {

    override suspend fun addFoundItem(item: FoundItem): Result<String> {
        val body = apiClient.json.encodeToString(item)
        val result = apiClient.postJson("/api/items/found", body, authRepository.token)
        return result.mapCatching { jsonStr ->
            apiClient.json.decodeFromString<IdResponse>(jsonStr).id
        }
    }

    override suspend fun addLostItem(item: LostItem): Result<String> {
        val body = apiClient.json.encodeToString(item)
        val result = apiClient.postJson("/api/items/lost", body, authRepository.token)
        return result.mapCatching { jsonStr ->
            apiClient.json.decodeFromString<IdResponse>(jsonStr).id
        }
    }

    override suspend fun getActiveFoundItems(lastTimestamp: Long?): Result<List<FoundItem>> {
        val endpoint = if (lastTimestamp != null) "/api/items/found?lastTimestamp=$lastTimestamp" else "/api/items/found"
        val result = apiClient.get(endpoint, authRepository.token)
        return result.mapCatching { jsonStr ->
            apiClient.json.decodeFromString<List<FoundItem>>(jsonStr)
        }
    }

    override suspend fun getActiveLostItems(lastTimestamp: Long?): Result<List<LostItem>> {
        val endpoint = if (lastTimestamp != null) "/api/items/lost?lastTimestamp=$lastTimestamp" else "/api/items/lost"
        val result = apiClient.get(endpoint, authRepository.token)
        return result.mapCatching { jsonStr ->
            apiClient.json.decodeFromString<List<LostItem>>(jsonStr)
        }
    }

    override suspend fun searchFoundItems(filters: SearchFilters): Result<List<FoundItem>> {
        val params = mutableListOf<String>()
        if (filters.query.isNotBlank()) params.add("query=${URLEncoder.encode(filters.query, "UTF-8")}")
        if (filters.category != null) params.add("category=${filters.category.name}")
        if (filters.location.isNotBlank()) params.add("location=${URLEncoder.encode(filters.location, "UTF-8")}")
        if (filters.dateFrom != null) params.add("dateFrom=${filters.dateFrom}")
        if (filters.dateTo != null) params.add("dateTo=${filters.dateTo}")

        val queryString = if (params.isNotEmpty()) "?${params.joinToString("&")}" else ""
        val result = apiClient.get("/api/items/found$queryString", authRepository.token)
        return result.mapCatching { jsonStr ->
            apiClient.json.decodeFromString<List<FoundItem>>(jsonStr)
        }
    }

    override suspend fun searchLostItems(filters: SearchFilters): Result<List<LostItem>> {
        val params = mutableListOf<String>()
        if (filters.query.isNotBlank()) params.add("query=${URLEncoder.encode(filters.query, "UTF-8")}")
        if (filters.category != null) params.add("category=${filters.category.name}")
        if (filters.location.isNotBlank()) params.add("location=${URLEncoder.encode(filters.location, "UTF-8")}")
        if (filters.dateFrom != null) params.add("dateFrom=${filters.dateFrom}")
        if (filters.dateTo != null) params.add("dateTo=${filters.dateTo}")

        val queryString = if (params.isNotEmpty()) "?${params.joinToString("&")}" else ""
        val result = apiClient.get("/api/items/lost$queryString", authRepository.token)
        return result.mapCatching { jsonStr ->
            apiClient.json.decodeFromString<List<LostItem>>(jsonStr)
        }
    }

    override suspend fun markAsClaimed(itemId: String, isLostItem: Boolean): Result<Unit> {
        val type = if (isLostItem) "lost" else "found"
        val result = apiClient.put("/api/items/$type/$itemId/claim", "{}", authRepository.token)
        return result.map { Unit }
    }

    override suspend fun getFoundItemById(itemId: String): Result<FoundItem> {
        val result = apiClient.get("/api/items/found/$itemId", authRepository.token)
        return result.mapCatching { jsonStr ->
            apiClient.json.decodeFromString<FoundItem>(jsonStr)
        }
    }

    override suspend fun getLostItemById(itemId: String): Result<LostItem> {
        val result = apiClient.get("/api/items/lost/$itemId", authRepository.token)
        return result.mapCatching { jsonStr ->
            apiClient.json.decodeFromString<LostItem>(jsonStr)
        }
    }

    override suspend fun getUserFoundItems(userId: String): Result<List<FoundItem>> {
        val result = apiClient.get("/api/users/$userId/items", authRepository.token)
        return result.mapCatching { jsonStr ->
            val res = apiClient.json.decodeFromString<UserItemsResponse>(jsonStr)
            res.found
        }
    }

    override suspend fun getUserLostItems(userId: String): Result<List<LostItem>> {
        val result = apiClient.get("/api/users/$userId/items", authRepository.token)
        return result.mapCatching { jsonStr ->
            val res = apiClient.json.decodeFromString<UserItemsResponse>(jsonStr)
            res.lost
        }
    }
}
