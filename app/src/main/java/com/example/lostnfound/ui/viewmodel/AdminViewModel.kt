package com.example.lostnfound.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostnfound.data.ItemRepository
import com.example.lostnfound.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val updatingUid: String? = null
)

class AdminViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getAllUsers()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                users = result.getOrDefault(emptyList()),
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun updateUserRole(uid: String, newRole: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(updatingUid = uid, error = null, successMessage = null)
            val result = repository.updateUserRole(uid, newRole)
            if (result.isSuccess) {
                val updatedUsers = _uiState.value.users.map {
                    if (it.uid == uid) it.copy(role = newRole) else it
                }
                _uiState.value = _uiState.value.copy(
                    updatingUid = null,
                    users = updatedUsers,
                    successMessage = "Role updated to $newRole"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    updatingUid = null,
                    error = result.exceptionOrNull()?.message ?: "Failed to update role"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}
