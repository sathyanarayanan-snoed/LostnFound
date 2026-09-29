package com.example.lostnfound.domain.model

data class SearchFilters(
    val query: String = "",
    val category: ItemCategory? = null,
    val dateFrom: Long? = null,
    val dateTo: Long? = null,
    val location: String = "",
    val itemType: ItemType = ItemType.ALL
)

enum class ItemType {
    ALL, LOST, FOUND
}
