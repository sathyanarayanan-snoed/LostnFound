package com.example.lostnfound.ui.viewmodel

import com.example.lostnfound.data.MockItemRepository
import com.example.lostnfound.domain.model.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserSearchViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: MockItemRepository
    private lateinit var viewModel: UserSearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = MockItemRepository()
        viewModel = UserSearchViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isEmpty() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.users.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun updateQuery_blank_clearsResultsImmediately() = runTest(testDispatcher) {
        viewModel.updateQuery("")
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.users.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun updateQuery_debouncesSearch() = runTest(testDispatcher) {
        viewModel.updateQuery("alex")
        assertEquals("alex", viewModel.uiState.value.query)
        advanceTimeBy(100)
        assertTrue(viewModel.uiState.value.users.isEmpty())
        advanceTimeBy(250)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun clearSearch_resetsQueryAndResults() = runTest(testDispatcher) {
        viewModel.updateQuery("test")
        advanceUntilIdle()
        viewModel.clearSearch()
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.users.isEmpty())
        assertFalse(state.isLoading)
    }
}
