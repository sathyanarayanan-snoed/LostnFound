package com.example.lostnfound.data

import com.example.lostnfound.data.firebase.FirebaseAuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseAuthRepository(
    private val authService: FirebaseAuthService
) : AuthRepository {

    override val currentUserEmail: String?
        get() = authService.currentUser?.email

    override val currentUserId: String?
        get() = authService.currentUser?.uid

    override val authStateFlow: Flow<String?> = authService.authStateFlow.map { it?.uid }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return authService.signIn(email, password).map { Unit }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<Unit> {
        return authService.signUp(email, password, displayName).map { Unit }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return authService.sendPasswordReset(email)
    }

    override fun signOut() {
        authService.signOut()
    }

    override fun isValidEmail(email: String): Boolean {
        return authService.isValidEmail(email)
    }
}
