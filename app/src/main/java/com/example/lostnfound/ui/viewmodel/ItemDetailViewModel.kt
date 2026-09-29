package com.example.lostnfound.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lostnfound.data.AuthRepository
import com.example.lostnfound.data.ItemRepository
import com.example.lostnfound.domain.model.FoundItem
import com.example.lostnfound.domain.model.LostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItemDetailUiState(
    val foundItem: FoundItem? = null,
    val lostItem: LostItem? = null,
    val isLostItem: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOwner: Boolean = false,
    val claimSuccess: Boolean = false,
    val showContactDialog: Boolean = false
)

class ItemDetailViewModel(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    fun loadItem(itemId: String, isLost: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isLostItem = isLost)
            val currentUserId = authRepository.currentUserId ?: ""

            if (isLost) {
                val result = itemRepository.getLostItemById(itemId)
                val item = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    lostItem = item,
                    isLoading = false,
                    isOwner = item?.reporterId == currentUserId,
                    error = result.exceptionOrNull()?.message
                )
            } else {
                val result = itemRepository.getFoundItemById(itemId)
                val item = result.getOrNull()
                _uiState.value = _uiState.value.copy(
                    foundItem = item,
                    isLoading = false,
                    isOwner = item?.reporterId == currentUserId,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun markAsClaimed(itemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = itemRepository.markAsClaimed(itemId, _uiState.value.isLostItem)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                claimSuccess = result.isSuccess,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun toggleContactDialog() {
        _uiState.value = _uiState.value.copy(showContactDialog = !_uiState.value.showContactDialog)
    }
}
