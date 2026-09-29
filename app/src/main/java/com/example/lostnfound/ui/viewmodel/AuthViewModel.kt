package com.example.lostnfound.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostnfound.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val isSignUpMode: Boolean = false,
    val userId: String? = null,
    val userEmail: String? = null,
    val displayName: String? = null,
    val role: String = "member",
    val profilePicUrl: String = "",
    val isAuthenticated: Boolean = false
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.authStateFlow.collect { userId ->
                _uiState.value = _uiState.value.copy(
                    userId = userId,
                    userEmail = if (userId != null) authRepository.currentUserEmail else null,
                    displayName = if (userId != null) authRepository.currentUserEmail?.substringBefore("@") else null,
                    role = if (userId != null) authRepository.currentUserRole ?: "member" else "member",
                    profilePicUrl = if (userId != null) authRepository.currentUserProfilePic ?: "" else "",
                    isAuthenticated = userId != null
                )
            }
        }
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isSignUpMode = !_uiState.value.isSignUpMode,
            error = null,
            message = null
        )
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)
            val result = authRepository.signIn(email, password)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                role = authRepository.currentUserRole ?: "member",
                profilePicUrl = authRepository.currentUserProfilePic ?: "",
                error = result.exceptionOrNull()?.let { mapAuthError(it) }
            )
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            if (!authRepository.isValidEmail(email)) {
                _uiState.value = _uiState.value.copy(
                    error = "Invalid format. Use name.deptYear@citchennai.net"
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)
            val result = authRepository.signUp(email, password, displayName)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                role = authRepository.currentUserRole ?: "member",
                profilePicUrl = authRepository.currentUserProfilePic ?: "",
                error = result.exceptionOrNull()?.let { mapAuthError(it) }
            )
        }
    }

    fun uploadProfilePic(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.updateProfilePic(uri)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profilePicUrl = result.getOrThrow()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to upload profile picture"
                )
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            if (email.isBlank() || !authRepository.isValidEmail(email)) {
                _uiState.value = _uiState.value.copy(
                    error = "Please enter your valid institutional email first"
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)
            val result = authRepository.sendPasswordReset(email)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Password reset link sent to your email"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.let { mapAuthError(it) }
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, message = null)
    }

    private fun mapAuthError(e: Throwable): String {
        val msg = e.message ?: "Authentication error"
        return when {
            msg.contains("The email address is already in use", ignoreCase = true) ->
                "Account already exists with this email"
            msg.contains("no user record", ignoreCase = true) || msg.contains("invalid-credential", ignoreCase = true) ->
                "Invalid credentials. Please verify your email and password."
            msg.contains("network", ignoreCase = true) ->
                "Network error. Please check your internet connection."
            else -> msg
        }
    }
}
