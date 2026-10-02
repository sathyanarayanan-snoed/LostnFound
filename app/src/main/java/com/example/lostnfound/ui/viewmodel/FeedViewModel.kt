package com.example.lostnfound.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostnfound.data.ItemRepository
import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.ItemType
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FeedItem(
    val id: String,
    val title: String,
    val category: String,
    val imageUrl: String,
    val location: String,
    val reportedAt: Long,
    val isLostItem: Boolean,
    val status: String
)

data class FeedUiState(
    val items: List<FeedItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val selectedTab: ItemType = ItemType.ALL,
    val searchFilters: SearchFilters = SearchFilters(),
    val hasMoreItems: Boolean = true
)

class FeedViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val feedItems = mutableListOf<FeedItem>()
            val filters = _uiState.value.searchFilters
            val hasSearch = filters.query.isNotBlank() || filters.category != null ||
                    filters.location.isNotBlank() || filters.dateFrom != null

            when (_uiState.value.selectedTab) {
                ItemType.ALL -> {
                    if (hasSearch) {
                        val foundResult = repository.searchFoundItems(filters)
                        val lostResult = repository.searchLostItems(filters)
                        foundResult.getOrNull()?.forEach { feedItems.add(it.toFeedItem()) }
                        lostResult.getOrNull()?.forEach { feedItems.add(it.toFeedItem()) }
                    } else {
                        val foundResult = repository.getActiveFoundItems()
                        val lostResult = repository.getActiveLostItems()
                        foundResult.getOrNull()?.forEach { feedItems.add(it.toFeedItem()) }
                        lostResult.getOrNull()?.forEach { feedItems.add(it.toFeedItem()) }
                    }
                }
                ItemType.FOUND -> {
                    val result = if (hasSearch) repository.searchFoundItems(filters)
                    else repository.getActiveFoundItems()
                    result.getOrNull()?.forEach { feedItems.add(it.toFeedItem()) }
                }
                ItemType.LOST -> {
                    val result = if (hasSearch) repository.searchLostItems(filters)
                    else repository.getActiveLostItems()
                    result.getOrNull()?.forEach { feedItems.add(it.toFeedItem()) }
                }
            }

            feedItems.sortByDescending { it.reportedAt }
            _uiState.value = _uiState.value.copy(
                items = feedItems,
                isLoading = false
            )
        }
    }

    fun selectTab(tab: ItemType) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        loadItems()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchFilters = _uiState.value.searchFilters.copy(query = query)
        )
    }

    fun applyFilters(filters: SearchFilters) {
        _uiState.value = _uiState.value.copy(searchFilters = filters)
        loadItems()
    }

    fun search() {
        loadItems()
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(searchFilters = SearchFilters())
        loadItems()
    }

    private fun FoundItem.toFeedItem() = FeedItem(
        id = id,
        title = description.take(80),
        category = category,
        imageUrl = imageUrl,
        location = placeFound,
        reportedAt = reportedAt,
        isLostItem = false,
        status = status
    )

    private fun LostItem.toFeedItem() = FeedItem(
        id = id,
        title = description.take(80),
        category = category,
        imageUrl = imageUrl,
        location = lastSeenLocation.ifBlank { "Campus" },
        reportedAt = reportedAt,
        isLostItem = true,
        status = status
    )
}
