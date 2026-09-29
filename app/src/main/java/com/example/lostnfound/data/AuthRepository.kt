package com.example.lostnfound.data

import kotlinx.coroutines.flow.Flow

data class MockUser(
    val uid: String,
    val email: String,
    val displayName: String
)

interface AuthRepository {
    val currentUserEmail: String?
    val currentUserId: String?
    val authStateFlow: Flow<String?>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, displayName: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    fun signOut()
    fun isValidEmail(email: String): Boolean
}
