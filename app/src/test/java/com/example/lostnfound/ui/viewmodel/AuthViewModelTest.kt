package com.example.lostnfound.ui.viewmodel

import com.example.lostnfound.data.MockAuthRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
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
class AuthViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: MockAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = MockAuthRepository()
        viewModel = AuthViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isNotAuthenticated() = runTest(testDispatcher) {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isAuthenticated)
        assertFalse(state.isSignUpMode)
        assertNull(state.userId)
    }

    @Test
    fun toggleMode_switchesSignUpState() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isSignUpMode)
        viewModel.toggleMode()
        assertTrue(viewModel.uiState.value.isSignUpMode)
        viewModel.toggleMode()
        assertFalse(viewModel.uiState.value.isSignUpMode)
    }

    @Test
    fun signIn_validCredentials_authenticatesUser() = runTest(testDispatcher) {
        advanceUntilIdle()
        viewModel.signIn("alex.cs2024@citchennai.net", "securePassword123")
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isAuthenticated)
        assertEquals("mock_user_123", state.userId)
        assertNull(state.error)
    }

    @Test
    fun signIn_invalidEmail_returnsError() = runTest(testDispatcher) {
        advanceUntilIdle()
        viewModel.signIn("invalid@gmail.com", "short")
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isAuthenticated)
        assertNotNull(state.error)
    }

    @Test
    fun signOut_resetsAuthState() = runTest(testDispatcher) {
        advanceUntilIdle()
        viewModel.signIn("alex.cs2024@citchennai.net", "securePassword123")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isAuthenticated)
        viewModel.signOut()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isAuthenticated)
    }
}
