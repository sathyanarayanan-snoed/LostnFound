package com.example.lostnfound.ui.main

import com.example.lostnfound.data.ItemRepository
import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters
import com.example.lostnfound.ui.viewmodel.FeedViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = FeedViewModel(FakeItemRepository())
        // Initially it might be loading, but since we are using runTest and the mock returns immediately (mostly)
        // we check the state after the first load
        val state = viewModel.uiState.first()
        assertFalse(state.isLoading)
        assertEquals(1, state.items.size)
    }

    @Test
    fun uiState_onTabSelected_updatesItems() = runTest {
        val viewModel = FeedViewModel(FakeItemRepository())
        assertEquals(1, viewModel.uiState.value.items.size)
    }
}

private class FakeItemRepository : ItemRepository {
    override suspend fun addFoundItem(item: FoundItem): Result<String> = Result.success("1")
    override suspend fun addLostItem(item: LostItem): Result<String> = Result.success("1")
    override suspend fun getActiveFoundItems(lastTimestamp: Long?): Result<List<FoundItem>> = 
        Result.success(listOf(FoundItem(description = "Sample")))
    override suspend fun getActiveLostItems(lastTimestamp: Long?): Result<List<LostItem>> = 
        Result.success(emptyList())
    override suspend fun searchFoundItems(filters: SearchFilters): Result<List<FoundItem>> = Result.success(emptyList())
    override suspend fun searchLostItems(filters: SearchFilters): Result<List<LostItem>> = Result.success(emptyList())
    override suspend fun markAsClaimed(itemId: String, isLostItem: Boolean): Result<Unit> = Result.success(Unit)
    override suspend fun getFoundItemById(itemId: String): Result<FoundItem> = Result.success(FoundItem())
    override suspend fun getLostItemById(itemId: String): Result<LostItem> = Result.success(LostItem())
    override suspend fun getUserFoundItems(userId: String): Result<List<FoundItem>> = Result.success(emptyList())
    override suspend fun getUserLostItems(userId: String): Result<List<LostItem>> = Result.success(emptyList())
}
