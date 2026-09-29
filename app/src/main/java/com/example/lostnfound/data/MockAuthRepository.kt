package com.example.lostnfound.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockAuthRepository : AuthRepository {
    private val _authStateFlow = MutableStateFlow<String?>(null)
    override val authStateFlow: StateFlow<String?> = _authStateFlow
    
    private var _email: String? = null
    override val currentUserEmail: String? get() = _email
    override val currentUserId: String? get() = _authStateFlow.value

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        delay(1000)
        return if (isValidEmail(email) && password.length >= 6) {
            _email = email
            _authStateFlow.value = "mock_user_123"
            Result.success(Unit)
        } else {
            Result.failure(Exception("Invalid credentials or email format. Use name.deptYear@citchennai.net"))
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<Unit> {
        delay(1000)
        return if (isValidEmail(email) && password.length >= 6) {
            _email = email
            _authStateFlow.value = "mock_user_123"
            Result.success(Unit)
        } else {
            Result.failure(Exception("Invalid registration details. Email must be @citchennai.net"))
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        delay(500)
        return if (isValidEmail(email)) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Invalid email address"))
        }
    }

    override fun signOut() {
        _email = null
        _authStateFlow.value = null
    }

    override fun isValidEmail(email: String): Boolean {
        return email.endsWith("@citchennai.net")
    }
}
