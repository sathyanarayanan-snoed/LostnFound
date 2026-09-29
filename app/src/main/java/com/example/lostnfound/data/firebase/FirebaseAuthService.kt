package com.example.lostnfound.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        const val ALLOWED_DOMAIN = "citchennai.net"
        const val EMAIL_REGEX = "^[a-zA-Z0-9]+\\.[a-zA-Z0-9]+@citchennai\\.net$"
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun isValidEmail(email: String): Boolean {
        return email.matches(EMAIL_REGEX.toRegex())
    }

    suspend fun signUp(email: String, password: String, displayName: String): Result<FirebaseUser> {
        return try {
            if (!isValidEmail(email)) {
                return Result.failure(Exception("Invalid email format. Use name.deptYear@citchennai.net"))
            }
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            val userData = hashMapOf(
                "uid" to user.uid,
                "email" to email,
                "displayName" to displayName,
                "createdAt" to System.currentTimeMillis(),
                "postCount" to 0,
                "lastPostTime" to 0L
            )
            firestore.collection("users").document(user.uid).set(userData).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
