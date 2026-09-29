package com.example.lostnfound.data

import android.content.Context
import android.content.SharedPreferences
import com.example.lostnfound.data.network.ApiClient
import com.example.lostnfound.data.network.AuthRequest
import com.example.lostnfound.data.network.AuthResponse
import com.example.lostnfound.data.network.ResetPasswordRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString

class HttpAuthRepository(
    private val context: Context,
    private val apiClient: ApiClient
) : AuthRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val _authStateFlow = MutableStateFlow<String?>(prefs.getString("user_id", null))
    override val authStateFlow: Flow<String?> = _authStateFlow.asStateFlow()

    override val currentUserEmail: String?
        get() = prefs.getString("user_email", null)

    override val currentUserId: String?
        get() = prefs.getString("user_id", null)

    val token: String?
        get() = prefs.getString("jwt_token", null)

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        val body = apiClient.json.encodeToString(AuthRequest(email, password))
        val result = apiClient.postJson("/api/auth/login", body)
        return result.mapCatching { jsonStr ->
            val authRes = apiClient.json.decodeFromString<AuthResponse>(jsonStr)
            saveSession(authRes.token, authRes.user.uid, authRes.user.email, authRes.user.displayName)
            Unit
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<Unit> {
        val body = apiClient.json.encodeToString(AuthRequest(email, password, displayName))
        val result = apiClient.postJson("/api/auth/signup", body)
        return result.mapCatching { jsonStr ->
            val authRes = apiClient.json.decodeFromString<AuthResponse>(jsonStr)
            saveSession(authRes.token, authRes.user.uid, authRes.user.email, authRes.user.displayName)
            Unit
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        val body = apiClient.json.encodeToString(ResetPasswordRequest(email))
        val result = apiClient.postJson("/api/auth/reset-password", body)
        return result.map { Unit }
    }

    override fun signOut() {
        prefs.edit().clear().apply()
        _authStateFlow.value = null
    }

    override fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && email.contains("@")
    }

    private fun saveSession(token: String, uid: String, email: String, displayName: String) {
        prefs.edit()
            .putString("jwt_token", token)
            .putString("user_id", uid)
            .putString("user_email", email)
            .putString("user_display_name", displayName)
            .apply()
        _authStateFlow.value = uid
    }
}
