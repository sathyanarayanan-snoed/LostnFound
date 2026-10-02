package com.example.lostnfound.ui.main

import com.example.lostnfound.data.ItemRepository
import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.ItemType
import com.example.lostnfound.domain.model.LostItem
import com.example.lostnfound.domain.model.SearchFilters
import com.example.lostnfound.domain.model.User
import com.example.lostnfound.ui.viewmodel.FeedViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initiallyLoading() = runTest(testDispatcher) {
        val viewModel = FeedViewModel(FakeItemRepository())
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.items.size)
    }

    @Test
    fun uiState_onTabSelected_updatesItems() = runTest(testDispatcher) {
        val viewModel = FeedViewModel(FakeItemRepository())
        advanceUntilIdle()
        viewModel.selectTab(ItemType.FOUND)
        advanceUntilIdle()
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
    override suspend fun searchUsers(query: String): Result<List<User>> = Result.success(emptyList())
    override suspend fun getAllUsers(): Result<List<User>> = Result.success(emptyList())
    override suspend fun updateUserRole(uid: String, role: String): Result<Unit> = Result.success(Unit)
    override suspend fun deleteItemAsAdmin(itemId: String, isLostItem: Boolean): Result<Unit> = Result.success(Unit)
}
